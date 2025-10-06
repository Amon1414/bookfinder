package com.example.bookfinder.repository

import com.example.bookfinder.constants.ErrorMessages
import com.example.bookfinder.error.exception.DbAccessException
import com.example.bookfinder.error.exception.ServiceUnavailableException
import com.example.bookfinder.model.BookModel
import com.example.db.tables.Author
import com.example.db.tables.AuthorBook
import com.example.db.tables.Book
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class BookRepository(val dslContext: DSLContext) {

    val book: Book = Book.BOOK
    val author: Author = Author.AUTHOR
    val authorBook: AuthorBook = AuthorBook.AUTHOR_BOOK

    /**
     * Select the books by author.
     *
     * @param authorId author ID.
     */
    fun getByAuthor(authorId: Int): List<BookModel> {
        try {
            return dslContext.select(
                author.ID,
                book.ID,
                book.TITLE,
                book.PRICE,
                author.NAME,
                book.IS_PUBLISHED
            ).from(author)
                .join(authorBook).on(author.ID.eq(authorBook.AUTHOR_ID))
                .join(book).on(authorBook.BOOK_ID.eq(book.ID))
                .where(author.ID.eq(authorId))
                .orderBy(book.ID)
                .fetch().map { selected ->
                    BookModel(
                        id = selected.get(book.ID),
                        title = selected.get(book.TITLE),
                        price = selected.get(book.PRICE),
                        isPublished = selected.get(book.IS_PUBLISHED),
                        authorIdList = emptyList()
                    )
                }
                .toList()
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }

    /**
     * Select the book by book id.
     *
     * @param bookId book id.
     */
    fun get(bookId: Int): BookModel {
        try {
            val selected = dslContext.select(
                book.ID,
                book.TITLE,
                book.PRICE,
                book.IS_PUBLISHED
            ).from(book)
                .where(book.ID.eq(bookId))
                .fetchOne() ?: throw RuntimeException(ErrorMessages.DB_ACCESS_SELECT_FAILED)
            return BookModel(
                id = selected.get(book.ID),
                title = selected.get(book.TITLE),
                price = selected.get(book.PRICE),
                isPublished = selected.get(book.IS_PUBLISHED),
                authorIdList = emptyList()
            )
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }

    /**
     * Insert the book.
     *
     * @param bookModel book model to register.
     */
    @Transactional
    fun register(bookModel: BookModel): BookModel {
        try {
            val book: Book = Book.BOOK
            val authorBook: AuthorBook = AuthorBook.AUTHOR_BOOK
            val newBookId = dslContext.nextval(DSL.name("book_id_seq"))
            val insertedBook = dslContext.insertInto(
                book,
                book.ID,
                book.TITLE,
                book.PRICE,
                book.IS_PUBLISHED,
                book.CREATED_DATE_TIME,
                book.UPDATED_DATE_TIME
            )
                .values(
                    newBookId.toInt(),
                    bookModel.vTitle,
                    bookModel.vPrice,
                    bookModel.vIsPublished,
                    LocalDateTime.now(),
                    LocalDateTime.now()
                )
                .returning(book.ID, book.TITLE, book.PRICE, book.IS_PUBLISHED)
                .fetchOne()?: throw RuntimeException(ErrorMessages.DB_ACCESS_INSERT_FAILED)
            val insertedAuthorIdList: List<Int> = bookModel.vAuthorIdList.map { authorId ->
                dslContext.insertInto(
                    authorBook,
                    authorBook.AUTHOR_ID,
                    authorBook.BOOK_ID,
                    authorBook.CREATED_DATE_TIME,
                    authorBook.UPDATED_DATE_TIME
                )
                    .values(
                        authorId,
                        newBookId.toInt(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                    .returning(authorBook.AUTHOR_ID)
                    .fetchOne()
                    ?.getValue(authorBook.AUTHOR_ID) ?: throw RuntimeException(ErrorMessages.DB_ACCESS_INSERT_FAILED)
            }
            return BookModel(
                id = insertedBook.get(book.ID),
                title = insertedBook.get(book.TITLE),
                price = insertedBook.get(book.PRICE),
                isPublished = insertedBook.get(book.IS_PUBLISHED),
                authorIdList = insertedAuthorIdList
            )
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }

    /**
     * Update the book.
     *
     * @param bookModel book model to update.
     */
    @Transactional
    fun update(bookModel: BookModel): BookModel {
        try {
            val book: Book = Book.BOOK
            val authorBook: AuthorBook = AuthorBook.AUTHOR_BOOK
            val updatedBook = dslContext.update(book)
                .set(book.TITLE, bookModel.vTitle)
                .set(book.PRICE, bookModel.vPrice)
                .set(book.IS_PUBLISHED, bookModel.vIsPublished)
                .set(book.UPDATED_DATE_TIME, LocalDateTime.now())
                .where(book.ID.eq(bookModel.vId))
                .returning(book.ID, book.TITLE, book.PRICE, book.IS_PUBLISHED)
                .fetchOne()?: throw RuntimeException(ErrorMessages.DB_ACCESS_UPDATE_FAILED)
            dslContext.delete(authorBook)
                .where(authorBook.BOOK_ID.eq(bookModel.vId)).execute()
            val updatedAuthorIdList: List<Int> = bookModel.vAuthorIdList.map { authorId ->
                dslContext.insertInto(
                    authorBook,
                    authorBook.AUTHOR_ID,
                    authorBook.BOOK_ID,
                    authorBook.CREATED_DATE_TIME,
                    authorBook.UPDATED_DATE_TIME
                )
                    .values(
                        authorId,
                        bookModel.vId,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                    )
                    .returning(authorBook.AUTHOR_ID)
                    .fetchOne()
                    ?.getValue(authorBook.AUTHOR_ID) ?: throw RuntimeException(ErrorMessages.DB_ACCESS_INSERT_FAILED)
            }
            return BookModel(
                id = updatedBook.get(book.ID),
                title = updatedBook.get(book.TITLE),
                price = updatedBook.get(book.PRICE),
                isPublished = updatedBook.get(book.IS_PUBLISHED),
                authorIdList = updatedAuthorIdList
            )
        } catch (ex: DataAccessResourceFailureException) {
            throw ServiceUnavailableException(ErrorMessages.DB_ACCESS_TEMPORARY_ERROR)
        } catch (ex: Exception) {
            throw DbAccessException(ErrorMessages.DB_ACCESS_ERROR)
        }
    }
}