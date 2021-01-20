package org.harry.jesus.fxutils.controls;

import com.itextpdf.text.pdf.parser.clipper.ClipperBase;
import generated.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.harry.jesus.fxutils.JesusMisc;
import org.harry.jesus.jesajautils.BibleReader;
import org.harry.jesus.jesajautils.Tuple;
import org.pmw.tinylog.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;


public class EditDictCompoundControl extends BorderPane {

    private Dictionary dictionary = new Dictionary();

    private ListView<String> itemsListView;

    private TextField idField;

    private TextField titleField;

    private ComboBox<String> refLinkBox;

    private ComboBox<String> seeBox;

    private TextArea descriptionText;

    private ContextMenu refLinkMenu;

    private ContextMenu seeMenu;

    private Button addToList;

    private Button newDict;

    private Button loadDict;

    private Button saveDict;

    private Button newItem;

    public EditDictCompoundControl() {
        super();
        GridPane centerPane = new GridPane();
        centerPane.setHgap(10);
        centerPane.setVgap(10);
        centerPane.setPadding(new Insets(20, 150, 10, 10));
        Label idLabel = new Label("Identifier");
        idField = new TextField();
        centerPane.add(idLabel, 0, 0);
        centerPane.add(idField, 1, 0);
        Label titleLabel = new Label("Title");
        titleField = new TextField();
        centerPane.add(titleLabel, 0, 1);
        centerPane.add(titleField, 1, 1);
        Label descriptionLab = new Label("Description::");
        descriptionText = new TextArea();
        centerPane.add(descriptionLab, 0, 2);
        centerPane.add(descriptionText, 1, 2, 2, 2);
        refLinkBox = new ComboBox();
        refLinkMenu = new ContextMenu();
        MenuItem item = new MenuItem("Paste");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    refLinkBox.getEditor().setText(clipboard.getString());
                }
            }
        });
        Label refLinkLab = new Label("Reference Links:");
        refLinkMenu.getItems().add(item);
        refLinkBox.setContextMenu(refLinkMenu);
        refLinkBox.setEditable(true);
        centerPane.add(refLinkLab, 0, 4);
        centerPane.add(refLinkBox, 1, 4);
        seeBox = new ComboBox();
        seeMenu = new ContextMenu();
        MenuItem seeItem = new MenuItem("Paste");
        seeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasString()) {
                    seeBox.getEditor().setText(clipboard.getString());
                }
            }
        });
        Label seeLab = new Label("Reference to Text:");
        seeMenu.getItems().add(seeItem);
        seeBox.setContextMenu(refLinkMenu);
        seeBox.setEditable(true);
        centerPane.add(seeLab, 0, 5);
        centerPane.add(seeBox, 1, 5);
        this.setCenter(centerPane);
        /**
         * The bottom
         */
        GridPane bottomPane = new GridPane();
        bottomPane.setHgap(10);
        bottomPane.setVgap(10);
        bottomPane.setPadding(new Insets(20, 150, 10, 10));
        addToList = new Button("<-- move item to list");
        newDict = new Button("New Dictionary");
        newItem = new Button(" New Item");
        loadDict = new Button("Load Dictionary");
        saveDict = new Button("Save Dictionary");
        this.setBottom(bottomPane);
        bottomPane.add(addToList, 0, 0);
        bottomPane.add(newDict, 1, 0);
        bottomPane.add(newItem, 2, 0);
        bottomPane.add(loadDict, 3, 0);
        bottomPane.add(saveDict, 4, 0);

        itemsListView = new ListView<>();
        this.setLeft(itemsListView);
        initLiseners();
    }

    public void initLiseners() {
       /** refLinkBox.getEditor().focusedProperty()
                .addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                refLinkBox.getItems().add(refLinkBox.getEditor().getText());
            }
        }); */

        refLinkBox.setOnAction(e -> {
            refLinkBox.getItems().add(refLinkBox.getValue());
        });
        /* now the next box*/
        /**seeBox.getEditor().focusedProperty()
                .addListener((obs, wasFocused, isNowFocused) -> {
                    if (! isNowFocused) {
                        seeBox.getItems().add(seeBox.getEditor().getText());
                    }
                }); */

        seeBox.setOnAction(e -> {
            seeBox.getItems().add(seeBox.getValue());
        });

        addToList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String idString = idField.getText();

                if (itemsListView.getItems().contains(idString)) {
                    int index = itemsListView.getItems().indexOf(idString);
                    TItem item = dictionary.getItem().get(index);
                    fillItem(item);
                } else {
                    TItem item = new TItem();
                    itemsListView.getItems().add(idString);
                    dictionary.getItem().add(item);
                    fillItem(item);
                }
            }
        });

        loadDict.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                InputStream inStream = JesusMisc.showOpenDialog(loadDict);
                dictionary = BibleReader.loadBibleDictionary(inStream);
                for(TItem item: dictionary.getItem()) {
                    itemsListView.getItems().add(item.getId());
                }
                if (itemsListView.getItems().size() >= 1) {
                    itemsListView.getSelectionModel().select(0);
                }
            }
        });

        itemsListView.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        if (newValue != null) {
                            int index = newValue.intValue();
                            TItem theItem = dictionary.getItem().get(index);
                            loadToFields(theItem);
                        }

                    }
                }
        );

        saveDict.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                OutputStream stream = JesusMisc.showSaveDialog(saveDict);
                if (stream != null) {
                    BibleReader.storeDictionary(dictionary, stream);
                }
            }
        });

        newItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearControls();
            }
        });

        newDict.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dictionary = new Dictionary();
                itemsListView.getItems().clear();
                clearControls();
            }
        });
    }

    private void clearControls() {
        descriptionText.setText("");
        refLinkBox.getEditor().setText("");
        refLinkBox.getItems().clear();
        seeBox.getEditor().setText("");
        seeBox.getItems().clear();
        idField.setText("");
        titleField.setText("");
    }

    private void fillItem(TItem item) {
        item.setId(idField.getText());
        TParagraph paragraph = new TParagraph();
        JAXBElement pElement = new JAXBElement(new QName("description"), TParagraph.class, paragraph);


        item.getContent().clear();
        item.getContent().add(pElement);

        MyAnyType title = new MyAnyType();
        title.getContent().add(titleField.getText());
        pElement = new JAXBElement(new QName("title"), MyAnyType.class, title);
        item.getContent().add(pElement);

        paragraph.getContent().clear();
        paragraph.getContent().add(descriptionText.getText());
        for (String link: refLinkBox.getItems()) {
            RefLinkType refLink = new RefLinkType();
            refLink.setMscope(link);
            pElement = new JAXBElement(new QName("reflink"), RefLinkType.class, refLink);
            paragraph.getContent().add(pElement);
        }
        for (String link: seeBox.getItems()) {
            SeeType see = new SeeType();
            see.setContent(link);
            pElement = new JAXBElement(new QName("see"), SeeType.class, see);
            paragraph.getContent().add(pElement);
        }
    }
    private void loadToFields(TItem item) {
        clearControls();
        List<Serializable> objects = item.getContent();
        idField.setText(item.getId());
        for (Serializable object : objects) {
            if (object instanceof JAXBElement) {
                JAXBElement jaxbElement = (JAXBElement) object;
                if (((JAXBElement) object).getName().getLocalPart().equals("description")) {
                    TParagraph thisElenent = (TParagraph) (((JAXBElement) object).getValue());
                    loadParagraph(thisElenent);
                } else if (jaxbElement.getName().getLocalPart().equals("title")) {
                    MyAnyType title = (MyAnyType) jaxbElement.getValue();
                    List<Serializable> contents = title.getContent();
                    StringBuffer titleBuffer = new StringBuffer();
                    for (Serializable cont : contents) {
                        if (cont instanceof String) {
                            titleBuffer.append((String) cont);
                        }
                    }
                    titleField.setText(titleBuffer.toString());
                }
            }
        }
    }

    private void loadParagraph(TParagraph paragraph) {
        int indexHyper = 0;
        List<Serializable> objects = paragraph.getContent();
        StringBuffer descriptionTextBuff = new StringBuffer();
        for (Serializable pContent : paragraph.getContent()) {
            if (pContent instanceof JAXBElement) {

                JAXBElement jaxbElement = (JAXBElement) pContent;
                Object thisContent = ((JAXBElement) pContent).getValue();
                if (jaxbElement.getName().getLocalPart().equals("see")) {
                    indexHyper = 0;
                    SeeType see = (SeeType) jaxbElement.getValue();
                    seeBox.getItems().add(see.getContent());
                } else if (thisContent instanceof BibLinkType) {

                } else if (thisContent instanceof RefLinkType) {

                    RefLinkType refLink = (RefLinkType) thisContent;
                    refLinkBox.getItems().add(refLink.getMscope());
                }

            } else if (pContent instanceof String) {
                descriptionTextBuff.append(pContent);
            }
        }
        descriptionText.setText(descriptionTextBuff.toString());
    }

}
