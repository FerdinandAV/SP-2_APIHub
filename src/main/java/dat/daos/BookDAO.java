package dat.daos;


import dat.dtos.BookDTO;
import dat.entities.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.TypedQuery;
import kotlin.collections.ArrayDeque;

import java.util.List;

public class BookDAO implements IDAO<BookDTO, Integer> {

    private static BookDAO instance;
    private static EntityManagerFactory emf;

    public static BookDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BookDAO();
        }
        return instance;
    }

    @Override
    public BookDTO read(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Book book = em.find(Book.class, integer);
            if (book == null) {
                throw new EntityNotFoundException("Book not found");
            }
            return new BookDTO(book);
        }
    }

    public List<BookDTO> readGenre(Book.Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            System.out.println("Genre: " + genre);
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b WHERE b.genre = :genre", BookDTO.class);
            query.setParameter("genre", genre);

            return query.getResultList();
        }
    }

    public BookDTO readTitle(String title) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b WHERE b.title = :title", BookDTO.class);
            query.setParameter("title", title);
            return query.getSingleResult();
        }
    }

    public List<BookDTO> readYear(Integer year) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b WHERE b.year = :year", BookDTO.class);
            query.setParameter("year", year);

            return query.getResultList();
        }
    }

    public List<BookDTO> readAuthor(String author) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b WHERE b.author = :author", BookDTO.class);
            query.setParameter("author", author);

            return query.getResultList();
        }
    }


    @Override
    public List<BookDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookDTO> query = em.createQuery("SELECT new dat.dtos.BookDTO(b) FROM Book b", BookDTO.class);

            return query.getResultList();
        }
    }

    @Override
    public BookDTO create(BookDTO bookDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book book = new Book(bookDTO);

            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class);
            query.setParameter("title", bookDTO.getTitle());
            //If there exists no books, persist it
            if (query.getResultList().isEmpty()) {
                em.persist(book);
            } else {
                book = query.getSingleResult();
            }

            em.getTransaction().commit();
            return new BookDTO(book);
        }
    }

    @Override
    public BookDTO update(Integer integer, BookDTO bookDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book book = em.find(Book.class, integer);
            book.setGenre(bookDTO.getGenre());
            book.setTitle(bookDTO.getTitle());
            book.setYear(bookDTO.getYear());
            Book mergedBook = em.merge(book);
            em.getTransaction().commit();
            return mergedBook != null ? new BookDTO(mergedBook) : null;

        }
    }

    @Override
    public void delete(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Book book = em.find(Book.class, integer);
            if (book != null) {
                em.remove(book);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        try (EntityManager em = emf.createEntityManager()) {
            Book book = em.find(Book.class, integer);

            return book != null;
        }
    }
}
