package org.harry.jesus.jesajautils;

import generated.Dictionary;
import generated.INFORMATION;
import generated.TINFORMATION;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;

public class AccordanceUtil {

    private final List<Tuple<String, Dictionary>> accordances ;

    public AccordanceUtil(List<Tuple<String, Dictionary>> accordances) {
        this.accordances = accordances;
    }


    public static String getIdFromInfo(TINFORMATION info) {
        List<JAXBElement<?>> elements =
                info.getTitleOrCreatorOrDescription();
        for (JAXBElement<?> element: elements) {
            if (element.getName().getLocalPart().equals("identifier")) {
                String id = (String)element.getValue();
                return id;
            }

        }
        return "";

    }

    public static String getIdFromBibleInfo(INFORMATION info) {
        List<JAXBElement<?>> elements =
                info.getTitleOrCreatorOrDescription();
        for (JAXBElement<?> element: elements) {
            if (element.getName().getLocalPart().equals("identifier")) {
                String id = (String)element.getValue();
                return id;
            }

        }
        return "";

    }

    public Optional<Tuple<String,Dictionary>> findAccordance(Dictionary dict, INFORMATION bibleInfo) {
        return this.accordances
                .stream()
                .filter(e -> getIdFromInfo(e.getSecond().getINFORMATION())
                        .contains(getIdFromBibleInfo(bibleInfo)))
                .findFirst();


    }

}
