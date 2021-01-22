package org.harry.jesus.fxutils.controls;

import generated.*;
import generated.Dictionary;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.harry.jesus.fxutils.JesusMisc;
import org.harry.jesus.fxutils.event.DeployDictionary;
import org.harry.jesus.jesajautils.AccordanceUtil;
import org.harry.jesus.jesajautils.BibleReader;
import org.harry.jesus.jesajautils.configjaxbser.DictionaryRef;
import org.tinylog.Logger;

import javax.swing.text.html.Option;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.*;
import java.time.LocalDate;
import java.util.*;


public class EditDictCompoundControl extends BorderPane {

    private Dictionary dictionary = new Dictionary();

    private List<TParagraph> descriptions = new ArrayList<>();

    private Map<Integer, List<TParagraph>> descrToItem = new HashMap<>();


    private ListView<String> itemsListView;

    private ListView<String> descrListView;

    private TextField idField;

    private TextField descrIdField;

    private TextField titleField;

    private TextField descriptionTitle;

    private ComboBox<String> refLinkBox;

    private ComboBox<String> seeBox;

    private TextArea descriptionText;

    private ContextMenu refLinkMenu;

    private ContextMenu seeMenu;

    private ContextMenu itemsListMenu;

    private Button addToList;

    private Button newDict;

    private Button loadDict;

    private Button saveDict;

    private Button newItem;

    private Button newDescription;

    private Button editInfo;

    private Button addToDescrList;

    private Integer actItemsIndex = 0;

    private Node nodeFromPrimary;

    private Optional<File> outFile = Optional.empty();

    public EditDictCompoundControl(Node nodeFromPrimary) {
        super();
        this.nodeFromPrimary = nodeFromPrimary;
        GridPane centerPane = new GridPane();
        centerPane.setHgap(10);
        centerPane.setVgap(10);
        centerPane.setPadding(new Insets(20, 150, 10, 10));

        Label idLabel = new Label("Identifier");
        idField = new TextField();
        centerPane.add(idLabel, 0, 0);
        centerPane.add(idField, 1, 0);

        Label titleLabel = new Label("Item Title");
        titleField = new TextField();
        centerPane.add(titleLabel, 0, 1);
        centerPane.add(titleField, 1, 1);

        Label descrIdLabel = new Label("Description Identifier");
        descrIdField = new TextField();
        centerPane.add(descrIdLabel, 0, 2);
        centerPane.add(descrIdField, 1, 2);

        Label descTitleLabel = new Label("Description Title");
        descriptionTitle = new TextField();
        centerPane.add(descTitleLabel, 0, 3);
        centerPane.add(descriptionTitle, 1, 3);

        Label descriptionLab = new Label("Description::");
        descriptionText = new TextArea();
        centerPane.add(descriptionLab, 0, 4);
        centerPane.add(descriptionText, 1, 4, 2, 2);
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
        centerPane.add(refLinkLab, 0, 6);
        centerPane.add(refLinkBox, 1, 6);
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
        centerPane.add(seeLab, 0, 7);
        centerPane.add(seeBox, 1, 7);
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
        newDescription = new Button(" New Description");
        loadDict = new Button("Load Dictionary");
        saveDict = new Button("Save Dictionary");
        editInfo = new Button("Edit dictionary Information");
        addToDescrList = new Button("Add description to list -->");
        this.setBottom(bottomPane);
        bottomPane.add(addToList, 0, 0);
        bottomPane.add(newDict, 1, 0);
        bottomPane.add(newItem, 2, 0);
        bottomPane.add(loadDict, 3, 0);
        bottomPane.add(saveDict, 4, 0);
        bottomPane.add(newDescription, 5, 0);
        bottomPane.add(editInfo, 6, 0);
        bottomPane.add(addToDescrList, 7, 0);

        itemsListView = new ListView<>();
        itemsListMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Remove selected Item");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               int index = itemsListView.getSelectionModel().getSelectedIndex();
               if (index >= 0) {
                   itemsListView.getItems().remove(index);
                   dictionary.getItem().remove(index);
               }
            }
        });
        itemsListMenu.getItems().add(deleteItem);
        deleteItem = new MenuItem("Deploy Dictionary");
        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (outFile.isPresent()) {
                    String dictID = AccordanceUtil
                            .getIdFromInfo(dictionary.getINFORMATION());
                    String dictName = AccordanceUtil
                            .getNameFromInfo(dictionary.getINFORMATION());
                    String fileName = outFile.get().getName();
                    fileName = fileName.substring(0, fileName.indexOf(".xml"));
                    DictionaryRef ref = new DictionaryRef()
                            .setDictionaryID(dictID)
                            .setDictionaryName(dictName)
                            .setPathToBook(outFile.get().getAbsolutePath())
                            .setFilename(fileName);
                    DeployDictionary eventToFire = new DeployDictionary(ref);
                    nodeFromPrimary.fireEvent(eventToFire);
                }

            }
        });
        itemsListMenu.getItems().add(deleteItem);
        itemsListView.setContextMenu(itemsListMenu);
        this.setLeft(itemsListView);
        descrListView = new ListView<>();
        this.setRight(descrListView);
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

        newDescription.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearDescrControls();
            }
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
                descrToItem.clear();
                outFile = JesusMisc.showOpenDialogFile(loadDict,
                        JesusMisc.FileExtension.XML_EXT);
                try {
                    InputStream inStream = new FileInputStream(outFile.get());
                    dictionary = BibleReader.loadBibleDictionary(inStream);
                    for (TItem item : dictionary.getItem()) {
                        itemsListView.getItems().add(item.getId());
                    }
                    if (itemsListView.getItems().size() >= 1) {
                        itemsListView.getSelectionModel().select(0);
                    }
                    actItemsIndex = 0;
                } catch (IOException ex) {
                    Logger.trace(ex);
                    Logger.trace("Load failed with: " + ex.getMessage());
                }
            }
        });

        itemsListView.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        if (newValue != null) {
                            clearControls();
                            int index = newValue.intValue();
                            TItem theItem = dictionary.getItem().get(index);
                            actItemsIndex = index;
                            loadToFields(theItem);
                        }

                    }
                }
        );

        descrListView.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable,
                                        Number oldValue, Number newValue) {
                        if (newValue != null) {
                            clearDescrControls();
                            int index = newValue.intValue();
                            TParagraph paragraph = descriptions.get(index);
                            loadParagraph(paragraph);
                        }

                    }
                }
        );

        saveDict.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveDictionary();
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

        editInfo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showInformationDiaog();
            }
        });

        addToDescrList.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String idString = descrIdField.getText();

                if (descrListView.getItems().contains(idString)) {
                    int index = descrListView.getItems().indexOf(idString);
                    TParagraph paragraph = descriptions.get(index);
                    fillParagraph(paragraph);
                } else {
                    TParagraph paragraph = new TParagraph();
                    descrListView.getItems().add(idString);
                    descriptions.add(paragraph);
                    fillParagraph(paragraph);
                }
                descrToItem.put(actItemsIndex, descriptions);
            }
        });

    }

    private void saveDictionary() {
        if (!outFile.isPresent()) {
            outFile = JesusMisc
                    .showSaveDialogFile(saveDict, JesusMisc.FileExtension.XML_EXT);
        }
        if (outFile.isPresent()) {
            try {
                OutputStream stream = new FileOutputStream(outFile.get());
                Integer index = 0;
                if (stream != null) {
                    BibleReader.storeDictionary(dictionary, stream);
                }
            } catch (IOException ex) {
                Logger.trace(ex);
                Logger.trace("Save failed with: " + ex.getMessage());

            }
        }
    }

    private void clearControls() {
        descriptions.clear();
        descrListView.getItems().clear();
        clearDescrControls();
        idField.setText("");
        titleField.setText("");
    }

    private void clearDescrControls() {
        descriptionText.setText("");
        descriptionTitle.setText("");
        descrIdField.setText("");
        refLinkBox.getEditor().setText("");
        refLinkBox.getItems().clear();
        seeBox.getEditor().setText("");
        seeBox.getItems().clear();
    }

    private void fillItem(TItem item) {
        item.setId(idField.getText());

        MyAnyType title = new MyAnyType();
        title.getContent().add(titleField.getText());
        JAXBElement pElement = new JAXBElement(new QName("title"), MyAnyType.class, title);
        item.getContent().clear();
        item.getContent().add(pElement);
        for (TParagraph paragraph: descriptions) {
            pElement = new JAXBElement(new QName("description"),
                    TParagraph.class, paragraph);
            item.getContent().add(pElement);

        }
    }

    private void fillParagraph(TParagraph paragraph) {
        JAXBElement pElement;
        paragraph.setId(descrIdField.getText());
        paragraph.getContent().clear();
        paragraph.getContent().add(descriptionText.getText());
        pElement = new JAXBElement(new QName("title"),
                String.class, descriptionTitle.getText());
        paragraph.getContent().add(pElement);
        for (String link: refLinkBox.getItems()) {
            RefLinkType refLink = new RefLinkType();
            refLink.setMscope(link);
            pElement = new JAXBElement(new QName("reflink"), RefLinkType.class, refLink);
            paragraph.getContent().add(pElement);
        }
        for (String link: seeBox.getItems()) {
            SeeType see = new SeeType();
            see.setContent(link);
            see.setTarget("x-self");
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
                    this.descriptions.add(thisElenent);
                    this.descrListView.getItems().add(thisElenent.getId());
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
        descrIdField.setText(paragraph.getId());
        StringBuffer descriptionTextBuff = new StringBuffer();
        for (Serializable pContent : paragraph.getContent()) {
            if (pContent instanceof JAXBElement) {

                JAXBElement jaxbElement = (JAXBElement) pContent;
                Object thisContent = ((JAXBElement) pContent).getValue();
                if (jaxbElement.getName().getLocalPart().equals("title")) {
                    String title = (String) thisContent;
                    descriptionTitle.setText(title);
                } else if (jaxbElement.getName().getLocalPart().equals("see")) {
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

    public void showInformationDiaog() {


        // Create the custom dialog.
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set dictionary information");


// Set the button types.
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.OK);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label titleLab = new Label("title:");
        TextField titleField = new TextField();
        grid.add(titleLab, 0, 0);
        grid.add(titleField, 1, 0);

        Label creatorLab = new Label("creator:");
        TextField creatorField = new TextField();
        grid.add(creatorLab, 0, 1);
        grid.add(creatorField, 1, 1);

        Label descriptionLab = new Label("description:");
        TextArea descriptionField = new TextArea();
        grid.add(descriptionLab, 0, 2);
        grid.add(descriptionField, 1, 2);

        Label idLab = new Label("identifier:");
        TextField idField = new TextField();
        grid.add(idLab, 0, 3);
        grid.add(idField, 1, 3);

        Label dateLab = new Label("date:");
        DatePicker datePicker = new DatePicker();
        grid.add(dateLab, 0, 4);
        grid.add(datePicker, 1, 4);

        Label rightsLab = new Label("rights:");
        TextField rightsField = new TextField();
        grid.add(rightsLab, 0, 5);
        grid.add(rightsField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        final TINFORMATION[] dictionaryInfo = {dictionary.getINFORMATION()};
        if (dictionaryInfo[0] != null) {
            for (JAXBElement element : dictionaryInfo[0].getTitleOrCreatorOrDescription()) {
                if (element.getName().getLocalPart().equals("title")) {
                    titleField.setText((String) element.getValue());
                } else if (element.getName().getLocalPart().equals("creator")) {
                    creatorField.setText((String) element.getValue());
                } else if (element.getName().getLocalPart().equals("description")) {
                    descriptionField.setText((String) element.getValue());
                } else if (element.getName().getLocalPart().equals("identifier")) {
                    idField.setText((String) element.getValue());
                } else if (element.getName().getLocalPart().equals("date")) {
                    XMLGregorianCalendar xmlDate =
                            (XMLGregorianCalendar) element.getValue();
                    LocalDate localDate = LocalDate.of(
                            xmlDate.getYear(),
                            xmlDate.getMonth(),
                            xmlDate.getDay());
                    datePicker.setValue(localDate);
                } else if (element.getName().getLocalPart().equals("rights")) {
                    rightsField.setText((String) element.getValue());
                }
            }




            /*for (JAXBElement element : dictionaryInfo.getTitleOrCreatorOrDescription()) {



                } else if (element.getName().getLocalPart().equals("publisher")) {
                    htmlBuffer.append("<br>publisher: " + element.getValue());

                } else if (element.getName().getLocalPart().equals("subject")) {
                    htmlBuffer.append("<br>subject: " + element.getValue());
                } else if (element.getName().getLocalPart().equals("contributors")) {
                    htmlBuffer.append("<br>contributors: " + element.getValue());
                } else if (element.getName().getLocalPart().equals("type")) {
                    htmlBuffer.append("<br>type: " + element.getValue());

                } else if (element.getName().getLocalPart().equals("format")) {
                    htmlBuffer.append("<br>format: " + element.getValue());
                } else if (element.getName().getLocalPart().equals("source")) {
                    htmlBuffer.append("<br>source: " + element.getValue());
                } else if (element.getName().getLocalPart().equals("language")) {
                    htmlBuffer.append("<br>language: " + element.getValue());
                } else if (element.getName().getLocalPart().equals("coverage")) {
                    htmlBuffer.append("<br>coverage: " + element.getValue());

                }
            }
            htmlBuffer.append("</span></p><hr>"); */
        }
        Platform.runLater(() -> titleField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (dictionaryInfo[0] == null) {
                    dictionaryInfo[0] = new TINFORMATION();
                    dictionary.setINFORMATION(dictionaryInfo[0]);
                }
                dictionaryInfo[0].getTitleOrCreatorOrDescription().clear();
                JAXBElement pElement =
                        new JAXBElement(new QName("title"),
                                String.class, titleField.getText());
                dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);
                pElement =
                        new JAXBElement(new QName("creator"),
                                String.class, creatorField.getText());
                dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);
                pElement =
                        new JAXBElement(new QName("description"),
                                String.class, descriptionField.getText());
                dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);
                pElement =
                        new JAXBElement(new QName("identifier"),
                                String.class, idField.getText());
                dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);
                LocalDate localDate = datePicker.getValue();
                if (localDate != null) {
                    try {
                        XMLGregorianCalendar xmlDate =
                                DatatypeFactory.newInstance()
                                        .newXMLGregorianCalendar(localDate.toString());
                        pElement =
                                new JAXBElement(new QName("date"),
                                        XMLGregorianCalendar.class, xmlDate);
                        dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }


                pElement =
                        new JAXBElement(new QName("rights"),
                                String.class, rightsField.getText());
                dictionaryInfo[0].getTitleOrCreatorOrDescription().add(pElement);


            }
            return null;
        });
        dialog.showAndWait();
    }
}
