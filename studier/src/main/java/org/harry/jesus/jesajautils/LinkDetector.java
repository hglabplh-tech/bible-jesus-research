package org.harry.jesus.jesajautils;

import jesus.harry.org.versnotes._1.Vers;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Link handler.
 */
public class LinkDetector {
    /**
     * Build vers link enhanced string.
     *
     * @param utils      the utils
     * @param bookNumber the book number
     * @param chapter    the chapter
     * @param versNoList the vers no list
     * @return the string
     */
    public static String buildVersLinkEnhanced(BibleTextUtils utils, Integer bookNumber, Integer chapter, List versNoList) {
        String versLink = "";
        StringBuffer linkBuffer = new StringBuffer();
        List<String> csv = utils.getBookLabels();
        Optional<String> book = csv.stream()
                .filter(e -> e.contains(bookNumber.toString()))
                .findFirst();
        if (book.isPresent()) {
            String [] split = book.get().split(",");
            List<Tuple<Integer, Integer>> rangesList = BibleTextUtils.detectVersesRangeForLink(versNoList);
            boolean first = true;
            for (Tuple<Integer, Integer> range: rangesList) {
                if (!first) {
                    linkBuffer.append(";");
                }
                if (range.getSecond() == 0) {
                    versLink = "[" + split[1] + " "
                            + chapter
                            + ","
                            + range.getFirst() + "]";
                } else {
                    versLink = "[" + split[1] + " "
                            + chapter
                            + ","
                            + range.getFirst() + "-" + range.getSecond() + "]";
                }
                first = false;
                linkBuffer.append(versLink);
            }
        }
        return linkBuffer.toString();
    }

    /**
     * Generate links fuzzy string.
     *
     * @param utils the utils
     * @param text  the text
     * @return the string
     */
    public static String generateLinksFuzzy(BibleTextUtils utils, final String text) {
        List<BibleTextUtils.BookLabel> labelList = getTransformedLabels(utils);
        List<BibleTextUtils.BookLink> links = parseLinks(utils, text);
        if (links.size() == 0) {
            links = parseLinksFuzzy(utils, text);
        }
        StringBuffer buffer = new StringBuffer();
        List<Vers> verses = new ArrayList<>();
        for (BibleTextUtils.BookLink link : links) {
            int index = utils.getBookLabels().indexOf(link.getBookLabel());
            BibleTextUtils.BookLabel theLabel = labelList.get(index);
            Vers vers = new Vers();
            vers.setChapter(BigInteger.valueOf(link.getChapter()));
            vers.setBook(BigInteger.valueOf(theLabel.getBookNumber()));
            for (Integer versNo: link.getVerses()) {
                vers.getVers().add(BigInteger.valueOf(versNo));
            }
            String linkString = utils.generateVersLink(Arrays.asList(vers), theLabel);
            buffer.append(linkString);
        }
        return buffer.toString();
    }

    /**
     * Parse links fuzzy list.
     *
     * @param utils the utils
     * @param text  the text
     * @return the list
     */
    public static List<BibleTextUtils.BookLink> parseLinksFuzzy(BibleTextUtils utils, final String text) {
        List<BibleTextUtils.BookLabel> labelList = getTransformedLabels(utils);
        List<BibleTextUtils.BookLink> bookLink = new ArrayList<>();
        boolean found = true;
        String bookString = null;
        String tempText;
        Map<String, List<Integer>> bookCol = LinkDetector.collectLabelsWithBeginIndex(utils, text);
        for (Map.Entry<String, List<Integer>> entry: bookCol.entrySet()) {
            tempText = text;
            found = true;
            bookString = entry.getKey();
            for (Integer startLink : entry.getValue()) {
                    int startLinks = -1;
                    int startNum = -1;
                    int startVers = -1;
                    int index = startLink;
                    while (index > -1) {
                        startLinks = index;
                        String newSub = tempText.substring((index + bookString.length() - 1));
                        index = newSub.indexOf(" ");
                        if (index > -1) {
                            startNum = index + startLinks + bookString.length();
                            String num = newSub.substring(index + 1).trim();
                            index = num.indexOf(",");
                            if (index == -1) {
                                tempText = "";
                            } else {
                                startVers = startNum + index + 1;
                                num = num.substring(0, index);
                                boolean isNumeric = num.matches("[0-9]+");
                                if (isNumeric) {
                                    Tuple<Integer, Integer> scanResult = cutString(text, startVers);
                                    int semicolon = scanResult.getSecond();
                                    int lastIndex = scanResult.getFirst();
                                    lastIndex = Math.min(lastIndex, text.length());
                                    String linkString = text.substring(startLinks, lastIndex);
                                    AtomicInteger lastIndexPoint = new AtomicInteger(0);
                                    String finalLinkString = linkString;
                                    Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9")
                                            .forEach(e -> {
                                                if (finalLinkString.lastIndexOf(e) > lastIndexPoint.get()) {
                                                    lastIndexPoint.set(finalLinkString.lastIndexOf(e));
                                                }

                                            });
                                    linkString = linkString.substring(0,
                                            Math.min(lastIndexPoint.get() + 1, linkString.length()));
                                    Optional<BibleTextUtils.BookLink> link = buildLink(utils, linkString);
                                    if (link.isPresent()) {
                                        System.out.println(linkString);
                                        bookLink.add(link.get());
                                    }
                                    found = true;
                                    if (semicolon == -1) {
                                        tempText = tempText.substring(lastIndex);
                                        if (tempText.isEmpty()) {
                                            index = -1;
                                        }
                                    } else {
                                        tempText = tempText.substring(semicolon).trim();
                                        index = tempText.indexOf(",");
                                        if (index > -1) {
                                            String nextNum = text.substring(0, index).trim();
                                            if (nextNum.matches("[0-9]+")) {
                                                tempText = bookString + " " + text;
                                            }
                                        }
                                    }
                                } else {
                                    index = -1;
                                    tempText = tempText.substring(startVers);
                                }
                            }
                        }

                    }

            }
        }

       return bookLink;
    }

    /**
     * Collect labels with begin index map.
     *
     * @param utils the utils
     * @param text  the text
     * @return the map
     */
    public static Map<String, List<Integer>> collectLabelsWithBeginIndex(BibleTextUtils utils, String text) {
        Map<String, List<Integer>> result = new LinkedHashMap<>();
        List<BibleTextUtils.BookLabel> labelList = getTransformedLabels(utils);
        for (BibleTextUtils.BookLabel label : labelList) {
            String bookString = null;
            if (text.contains(bookStrMapping(label.getLongName()))) {
                bookString = bookStrMapping(label.getLongName());

            } else if (text.contains(bookStrMapping(label.getShortName()))) {
                bookString = bookStrMapping(label.getShortName());
            }
            if (bookString != null) {
                int index = text.indexOf(bookString);
                List<Integer> hits = new ArrayList<>();
                while (index > -1) {
                    hits.add(index);
                    index = text.indexOf(bookString, index + bookString.length());
                }
                result.put(bookString, hits);
            }
        }
        return result;
    }

    /**
     * Cut string tuple.
     *
     * @param toCut     the to cut
     * @param startVers the start vers
     * @return the tuple
     */
    public static Tuple<Integer, Integer> cutString (String toCut, int startVers) {
        String lastPart = toCut.substring(startVers);
        int semiColonInd = lastPart.indexOf(";");
        int blankInd = lastPart.indexOf(" ");
        int bracketInd = lastPart.indexOf(")");
        int exclamationInd = lastPart.indexOf("!");
        int dotInd = lastPart.indexOf("\\.");
        int min = -1;
        int nextLinkINQueue = -1;
        if (semiColonInd != -1 && blankInd != -1) {
            min = Math.min(semiColonInd, blankInd);
            if (min == semiColonInd) {
                nextLinkINQueue = semiColonInd + startVers + 1;
            }
        } else if (semiColonInd > -1) {
            min = semiColonInd;
        } else if (blankInd > -1){
            min = blankInd;
        } else if (bracketInd > -1){
            min = bracketInd;
        } else if (exclamationInd > -1){
            min = exclamationInd;
        } else if (dotInd > -1){
            min = dotInd;
        } else {
            min = lastPart.length();
        }
        String result;
        if (min > -1) {
            int res = startVers + min;
            return new Tuple<>(res, nextLinkINQueue);
        } else {
            int res = startVers + lastPart.length();
            return new Tuple<>(res, nextLinkINQueue);
        }
    }

    /**
     * Parse links list.
     *
     * @param bibleTextUtils the bible text utils
     * @param text           the text
     * @return the list
     */
    public static List<BibleTextUtils.BookLink> parseLinks(BibleTextUtils bibleTextUtils, String text) {
        List<BibleTextUtils.BookLink> links = new ArrayList<>();
        int start = 0;
        int startLink = 0;
        while (startLink != -1) {
            startLink = text.indexOf("[", start);
            if (startLink != -1) {
                int endLink = text.indexOf("]", startLink);
                if (endLink != -1) {
                    String linkString = text.substring(startLink + 1, endLink);
                    Optional<BibleTextUtils.BookLink> link = buildLink(bibleTextUtils, linkString);
                    if (link.isPresent()) {
                        links.add(link.get());
                    }
                    start = endLink + 1;
                } else {
                    startLink = -1;
                }
            }
        }
        return links;
    }

    private static Optional<BibleTextUtils.BookLink> buildLink(BibleTextUtils bibleTextUtils, String linkString) {
        int start = 0;
        int endIndex = linkString.lastIndexOf(" ");
        if (endIndex > -1) {
            String bookStr = linkString.substring(start, endIndex);
            Optional<String> label = bibleTextUtils.getBookLabels().stream().filter(e ->
                    e.contains(bookStrMapping(bookStr))).findFirst();
            String temp = linkString.substring(endIndex + 1);
            endIndex = temp.indexOf(",");
            if (endIndex > -1) {
                String chapStr = temp.substring(0, endIndex).trim();
                start = endIndex + 1;
                boolean matches = chapStr.matches("[0-9]+");
                boolean processed = false;
                if (matches) {
                    Integer chapter = Integer.parseInt(chapStr);
                    String versesStr = temp.substring(start);
                    String[] single = versesStr.split("\\.");

                    String[] range = versesStr.split("-");
                    List<Integer> verses = new ArrayList<>();
                    if (single.length == 1 && range.length == 1) {
                        String numtemp = "";
                        if (single.length == 1) {
                            numtemp = single[0];
                        } else {
                            numtemp = range[0];
                        }

                        if (numtemp.matches("[0-9]+")) {
                            verses.add(Integer.parseInt(numtemp));
                            processed = true;
                        }
                    }
                    else if (range.length == 2) {
                        String startStr = range[0];
                        String endStr = range[1];
                        int sRange = Integer.parseInt(startStr);
                        int eRange = Integer.parseInt(endStr);
                        for (Integer vers = sRange; vers <= eRange; vers++) {
                            verses.add(vers);
                        }
                        processed = true;
                    } else if (single.length >1) {
                        for (String verseStr: single) {
                            int vers = Integer.parseInt(verseStr);
                            verses.add(vers);
                        }
                        processed = true;
                    }
                    if (processed) {
                        if (label.isPresent()) {
                            return Optional.of(new BibleTextUtils.BookLink(label.get(), chapter, verses));
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static String bookStrMapping(String book) {
        Map<String, String> map = new HashMap<>();
        map.put("1.Mo", "1. Mose");
        map.put("1.Mose", "1. Mose");
        map.put("2.Mo", "2. Mose");
        map.put("2.Mose", "2. Mose");
        map.put("3.Mo", "3. Mose");
        map.put("3.Mose", "3. Mose");
        map.put("4.Mo", "4. Mose");
        map.put("4.Mose", "4. Mose");
        map.put("5.Mo", "5. Mose");
        map.put("5.Mose", "5. Mose");
        map.put("1.Ch", "1. Chr");
        map.put("2.Ch", "2. Chr");
        map.put("1.Chr", "1. Chr");
        map.put("2.Chr", "2. Chr");
        map.put("Römer", "Roemer");
        map.put("Matthäus", "Matthaeus");
        map.put("Hebräer", "Hebraeer");
        if (map.get(book) != null) {
            return map.get(book);
        } else {
            return book;
        }

    }

    private static List<BibleTextUtils.BookLabel> getTransformedLabels(BibleTextUtils utils) {
        List<BibleTextUtils.BookLabel> labelList = new ArrayList<>();
        for (String label: utils.getBookLabels()) {
            labelList.add(new BibleTextUtils.BookLabel(label));
        }
        return labelList;
    }
}
