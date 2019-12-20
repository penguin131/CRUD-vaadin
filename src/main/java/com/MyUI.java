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
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import java.util.List;
import java.util.Objects;

@Theme("mytheme")
public class MyUI extends UI {
    private Grid<AuthorsEntity> authorsEntityGrid = new Grid<>(AuthorsEntity.class);
    private final VerticalLayout generalLayout = new VerticalLayout();
    private final HorizontalLayout upperLayout = new HorizontalLayout();
    private FormLayout newAuthorForm = new FormLayout();
    private Button addAuthorButton = new Button("Add new author");
    private Button deleteButton = new Button("Delete selected items");
    private GridLayout topGrid = new GridLayout(1, 1);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setAuthorsEntityGrid();
        createTopGrid();
        setNewAuthorForm();
        authorsEntityGrid.getEditor().setBuffered(true);
        upperLayout.setWidth("100%");
        generalLayout.addComponent(upperLayout);
        generalLayout.addComponent(authorsEntityGrid);
        generalLayout.addComponent(newAuthorForm);
        generalLayout.addComponent(deleteButton);
        upperLayout.addComponent(topGrid);
        setContent(generalLayout);
    }

    private void setAuthorsEntityGrid() {
        authorsEntityGrid.setVisible(true);
        authorsEntityGrid.setItems(
                Objects.requireNonNull(DatabaseActions.findAllAuthors())
        );
        authorsEntityGrid.getColumn("name").setHidden(true);
        authorsEntityGrid.getColumn("authorId").setCaption("Id");
        //table delete
        MultiSelectionModel<AuthorsEntity> selectionModel = (MultiSelectionModel<AuthorsEntity>)
                authorsEntityGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        deleteButton.addClickListener(clickEvent -> {
            for (AuthorsEntity author : selectionModel.getSelectedItems()) {
                DatabaseActions.deleteAuthorForId(author.getAuthorId());
                authorsEntityGrid.setItems(Objects.requireNonNull(DatabaseActions.findAllAuthors()));
            }
            authorsEntityGrid.setItems(Objects.requireNonNull(DatabaseActions.findAllAuthors()));
        });
        //edit mode
        authorsEntityGrid.getEditor().setEnabled(true);//включаю режим с выпадающим окошком
        Editor<AuthorsEntity> editor = authorsEntityGrid.getEditor();
        editor.addSaveListener(editorSaveEvent -> {
            DatabaseActions.updateAuthor(editorSaveEvent.getBean());
            authorsEntityGrid.setItems(Objects.requireNonNull(DatabaseActions.findAllAuthors()));
        });
        TextField doneField = new TextField();
        Binder<AuthorsEntity> binder = authorsEntityGrid.getEditor().getBinder();
        binder.bind(doneField, AuthorsEntity::getName, AuthorsEntity::setName);
        Binder.Binding<AuthorsEntity, String> doneBinding =
                binder.bind(doneField, AuthorsEntity::getName, AuthorsEntity::setName);
        authorsEntityGrid.addColumn(author -> String.valueOf(author.getName()))
                .setCaption("new author")
                .setWidth(345)
                .setEditorBinding(doneBinding);
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
                        for (ValidationResult error : errors) {
                            Notification.show(error.getErrorMessage());
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

    private void createTopGrid() {
        addAuthorButton.addClickListener(clickEvent ->
                switchVisible());
        topGrid.setWidth("100%");
        topGrid.addComponent(addAuthorButton, 0, 0);
        topGrid.setComponentAlignment(addAuthorButton, Alignment.TOP_LEFT);
    }

    private void switchVisible() {
        if (!authorsEntityGrid.isVisible()) {
            addAuthorButton.setCaption("Add new author");
            authorsEntityGrid.setItems(Objects.requireNonNull(DatabaseActions.findAllAuthors()));
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
