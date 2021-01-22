package org.harry.jesus.jesajautils;

import generated.Dictionary;
import generated.INFORMATION;
import generated.TINFORMATION;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;

/**
 * The type Accordance util.
 */
public class AccordanceUtil {

    private final List<Tuple<String, Dictionary>> accordances ;

    /**
     * Instantiates a new Accordance util.
     *
     * @param accordances the accordances
     */
    public AccordanceUtil(List<Tuple<String, Dictionary>> accordances) {
        this.accordances = accordances;
    }


    /**
     * Gets id from info.
     *
     * @param info the info
     * @return the id from info
     */
    public static String getIdFromInfo(TINFORMATION info) {
        if (info != null) {
            List<JAXBElement<?>> elements =
                    info.getTitleOrCreatorOrDescription();
            for (JAXBElement<?> element : elements) {
                if (element.getName().getLocalPart().equals("identifier")) {
                    String id = (String) element.getValue();
                    return id;
                }

            }
        }
        return "none";

    }

    /**
     * Gets name from info.
     *
     * @param info the info
     * @return the name from info
     */
    public static String getNameFromInfo(TINFORMATION info) {
        if (info != null) {
            List<JAXBElement<?>> elements =
                    info.getTitleOrCreatorOrDescription();
            for (JAXBElement<?> element : elements) {
                if (element.getName().getLocalPart().equals("title")) {
                    String id = (String) element.getValue();
                    return id;
                }

            }
        }
        return "none";

    }

    /**
     * Gets id from bible info.
     *
     * @param info the info
     * @return the id from bible info
     */
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

    /**
     * Find accordance optional.
     *
     * @param dict      the dict
     * @param bibleInfo the bible info
     * @return the optional
     */
    public Optional<Tuple<String,Dictionary>> findAccordance(Dictionary dict, INFORMATION bibleInfo) {
        return this.accordances
                .stream()
                .filter(e -> getIdFromInfo(e.getSecond().getINFORMATION())
                        .contains(getIdFromBibleInfo(bibleInfo)))
                .findFirst();


    }

}
