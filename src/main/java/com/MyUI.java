package com;

import javax.servlet.annotation.WebServlet;
import com.model.AuthorsEntity;
import com.model.DatabaseActions;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import java.util.List;

@Theme("mytheme")
public class MyUI extends UI {
    private Grid<AuthorsEntity> authorsEntityGrid = new Grid<>(AuthorsEntity.class);
    private final VerticalLayout layout = new VerticalLayout();
    private final HorizontalLayout nestedLayout = new HorizontalLayout();
    private FormLayout newAuthorForm = new FormLayout();
    private Button addAuthorButton = new Button("Add new author");
    private Button deleteButton = new Button("Delete selected items");

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
        authorsEntityGrid.setColumns("authorId", "name");
        authorsEntityGrid.setItems(
                DatabaseActions.findAllAuthors()
        );
        authorsEntityGrid.setVisible(true);
        MultiSelectionModel<AuthorsEntity> selectionModel
                = (MultiSelectionModel<AuthorsEntity>) authorsEntityGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        selectionModel.addMultiSelectionListener(event -> {
            Notification.show(String.valueOf(selectionModel.getSelectedItems().size()));
        });
        layout.addComponent(deleteButton);
        addAuthorButton.addClickListener(clickEvent -> {
            for (AuthorsEntity author : selectionModel.getSelectedItems()) {
                DatabaseActions.deleteAuthorForId(author.getAuthorId());
            }
            authorsEntityGrid.setItems(DatabaseActions.findAllAuthors());
        });
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
                        DatabaseActions.insertAuthor(newAuthor);
                        newAuthor.setName("");
                        switchVisible();
                    } catch (ValidationException e) {
                        List<ValidationResult> errors = e.getValidationErrors();
                        for (int i = 0; i < errors.size(); i++) {
                            Notification.show(errors.get(i).getErrorMessage());
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
            authorsEntityGrid.setItems(DatabaseActions.findAllAuthors());
        } else {
            addAuthorButton.setCaption("Go back!");
        }
        newAuthorForm.setVisible(!newAuthorForm.isVisible());
        authorsEntityGrid.setVisible(!authorsEntityGrid.isVisible());
        deleteButton.setVisible(!deleteButton.isVisible());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
