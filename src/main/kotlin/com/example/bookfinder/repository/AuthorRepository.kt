package com.example.bookfinder.repository

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.error.exception.ServiceUnavailableException
import com.example.bookfinder.model.AuthorModel
import com.example.db.tables.Author
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class AuthorRepository(val dslContext: DSLContext) {

    fun register(authorModel: AuthorModel): AuthorModel {
        try {
            val author: Author = Author.AUTHOR
            val newId = dslContext.nextval(DSL.name("author_id_seq"))
            val inserted = dslContext.insertInto(
                author,
                author.ID,
                author.NAME,
                author.BIRTH_DATE,
                author.CREATED_DATE_TIME,
                author.UPDATED_DATE_TIME
            )
                .values(newId.toInt(), authorModel.name, authorModel.birthDate, LocalDateTime.now(), LocalDateTime.now())
                .returning(author.ID, author.NAME, author.BIRTH_DATE)
                .fetchOne()?: throw RuntimeException(ErrorMessages.DB_ACCESS_INSERT_FAILED)
            return AuthorModel(
                id = inserted.getValue(Author.AUTHOR.ID),
                name = inserted.getValue(Author.AUTHOR.NAME),
                birthDate = inserted.getValue(Author.AUTHOR.BIRTH_DATE),
            )
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }

    fun update(authorModel: AuthorModel): AuthorModel {
        try {
            val author: Author = Author.AUTHOR
            val updated = dslContext.update(author)
                .set(author.NAME, authorModel.name)
                .set(author.BIRTH_DATE, authorModel.birthDate)
                .set(author.UPDATED_DATE_TIME, LocalDateTime.now())
                .where(author.ID.eq(authorModel.id))
                .returning(author.ID, author.NAME, author.BIRTH_DATE)
                .fetchOne()?: throw RuntimeException(ErrorMessages.DB_ACCESS_UPDATE_FAILED)
            return AuthorModel(
                id = updated.getValue(Author.AUTHOR.ID),
                name = updated.getValue(Author.AUTHOR.NAME),
                birthDate = updated.getValue(Author.AUTHOR.BIRTH_DATE),
            )
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }
}
