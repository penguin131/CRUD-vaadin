package com;

import javax.servlet.annotation.WebServlet;
import com.model.AuthorsEntity;
import com.model.DatabaseActions;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.Setter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.utils.HibernateUtil;
import org.hibernate.Session;
import java.util.List;

@Theme("mytheme")
public class MyUI extends UI {
    private Grid<AuthorsEntity> authorsEntityGrid = new Grid<>(AuthorsEntity.class);
    private final VerticalLayout layout = new VerticalLayout();
    private final HorizontalLayout nestedLayout = new HorizontalLayout();
    private FormLayout newAuthorForm = new FormLayout();
    private Button addAuthorButton = new Button("Add new author");

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        nestedLayout.setWidth("100%");
        layout.addComponent(nestedLayout);
        layout.addComponent(authorsEntityGrid);
        setAuthorsEntityGrid();

        createNewAuthorButton();

        layout.addComponent(newAuthorForm);
        setNewAuthorForm();


        setContent(layout);
    }

    private void setAuthorsEntityGrid() {
        final Session session = HibernateUtil.getHibernateSession();
        session.beginTransaction();
        List authors = session
                .createQuery("from AuthorsEntity")
                .list();
        session.close();
        authorsEntityGrid.setColumns("authorId", "name");
        authorsEntityGrid.setItems(
                authors
        );
        authorsEntityGrid.setVisible(true);
    }

    private void setNewAuthorForm() {
        Binder<AuthorsEntity> binder = new Binder<>();
        TextField authorName = new TextField("New author name:");
        AuthorsEntity newAuthor = new AuthorsEntity();
        binder.forField(authorName)
                .withValidator(name -> name.length() > 0, "fill name, PLEASE!")
                .bind(AuthorsEntity::getName, AuthorsEntity::setName);
        binder.readBean(newAuthor);
        Button saveButton = new Button("Save",
                event -> {
                    try {
                        binder.writeBean(newAuthor);
                        DatabaseActions.insertAuthorToDB(newAuthor);
                        newAuthor.setName("");
                        switchVisible();
                    } catch (ValidationException e) {
                        List errors = e.getValidationErrors();
                        for (int i = 0; i < errors.size(); i++) {
                            Notification.show(e.getValidationErrors().get(i).getErrorMessage());
                        }
                    }
                });
        Button resetButton = new Button("Reset", event -> binder.readBean(newAuthor));
        binder.bind(authorName, AuthorsEntity::getName, AuthorsEntity::setName);
        binder.bind(authorName,
                (ValueProvider<AuthorsEntity, String>) AuthorsEntity::getName,
                (Setter<AuthorsEntity, String>) AuthorsEntity::setName);
        newAuthorForm.addComponents(authorName, saveButton, resetButton);
        newAuthorForm.setVisible(false);
    }

    private void createNewAuthorButton() {
        GridLayout localGrid = new GridLayout(2, 2);
        Button logout = new Button("Logout");

        addAuthorButton.addClickListener(clickEvent ->
                switchVisible());
        localGrid.setWidth("100%");
        localGrid.addComponent(addAuthorButton, 0, 0);
        localGrid.addComponent(logout, 1, 0);
        localGrid.setComponentAlignment(addAuthorButton, Alignment.TOP_LEFT);
        localGrid.setComponentAlignment(logout, Alignment.TOP_RIGHT);
        nestedLayout.addComponent(localGrid);
    }

    private void switchVisible() {
        if (!authorsEntityGrid.isVisible()) {
            addAuthorButton.setCaption("Add new author");
            setAuthorsEntityGrid();
        } else {
            addAuthorButton.setCaption("Go back!");
            authorsEntityGrid.setVisible(!authorsEntityGrid.isVisible());
        }
        newAuthorForm.setVisible(!newAuthorForm.isVisible());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
