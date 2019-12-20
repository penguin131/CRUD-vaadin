package com.model;

import com.utils.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.util.List;

public class DatabaseActions {
	public static AuthorsEntity getAuthorForId(int authorID) {
		AuthorsEntity author = null;
		EntityManager entityManager;

		try {
			final Session session = HibernateUtil.getHibernateSession();
			session.beginTransaction();
			List authors = session
					.createQuery("from AuthorsEntity where authorID=" + authorID)
					.list();
			author = (AuthorsEntity) authors.get(0);
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return author;
	}

	public static void insertAuthor(AuthorsEntity author) {
		try {
			final Session session = HibernateUtil.getHibernateSession();
			session.beginTransaction();
			session.save(author);
			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<AuthorsEntity> findAllAuthors() {
		try {
			final Session session = HibernateUtil.getHibernateSession();
			session.beginTransaction();
			List<AuthorsEntity> authors = session
					.createQuery("from AuthorsEntity")
					.list();
			session.close();
			return authors;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void deleteAuthorForId(int authorID) {
		try {
			final Session session = HibernateUtil.getHibernateSession();
			session.beginTransaction();
			AuthorsEntity author = getAuthorForId(authorID);
			session.delete(author);
			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void updateAuthor(AuthorsEntity newAuthor) {
		try {
			final Session session = HibernateUtil.getHibernateSession();
			session.beginTransaction();
			AuthorsEntity hibernateAuthor = (AuthorsEntity) session
					.createQuery("from AuthorsEntity where authorId="+newAuthor.getAuthorId())
					.list()
					.get(0);
			hibernateAuthor.setName(newAuthor.getName());
			session.getTransaction().commit();
			session.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
