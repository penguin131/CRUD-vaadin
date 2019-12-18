package com.model;

import com.utils.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

public class DatabaseActions {
	public static AuthorsEntity getAuthorForId(int authorID) {
		final Session session = HibernateUtil.getHibernateSession();
		session.beginTransaction();
		AuthorsEntity author = null;
		List authors = session
				.createQuery("from AuthorsEntity where authorID=" + authorID)
				.list();
		if (authors.size() > 0) {
			author = (AuthorsEntity) authors.get(0);
		}
		session.close();
		return author;
	}

	public static void insertAuthorToDB(AuthorsEntity author) {
		final Session session = HibernateUtil.getHibernateSession();
		session.beginTransaction();
		session.save(author);
		session.getTransaction().commit();
		session.close();
	}
}
