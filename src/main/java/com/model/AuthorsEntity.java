package com.model;

import javax.persistence.*;

@Entity
@Table(name = "AUTHORS", schema = "DB_BOOKS")
public class AuthorsEntity {
	private int authorId;
	private String name;

	@Id
	@Column(name = "authorId", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	@Basic
	@Column(name = "name", length = 255)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AuthorsEntity that = (AuthorsEntity) o;
		if (authorId != that.authorId) return false;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = authorId;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
