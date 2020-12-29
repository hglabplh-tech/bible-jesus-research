package org.harry.jesus.jesajautils;

import jesus.harry.org.versnotes._1.Vers;

import java.math.BigInteger;
import java.util.*;

public class LinkHandler {
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

    public static String generateLinksFuzzy(BibleTextUtils utils, String text) {
        List<BibleTextUtils.BookLabel> labelList = getTransformedLabels(utils);
        List<BibleTextUtils.BookLink> links = parseLinksFuzzy(utils, text);
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

    public static List<BibleTextUtils.BookLink> parseLinksFuzzy(BibleTextUtils utils, String text) {
        List<BibleTextUtils.BookLabel> labelList = getTransformedLabels(utils);
        List<BibleTextUtils.BookLink> bookLink = new ArrayList<>();
        boolean found = true;
        String bookString = null;
        while(found) {
            found = false;
            for (BibleTextUtils.BookLabel label : labelList) {
                if (text.contains(label.getLongName())) {
                    bookString = label.getLongName();
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (BibleTextUtils.BookLabel label : labelList) {
                    if (text.contains(label.getShortName())) {
                        bookString = label.getShortName();
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                text = "";
                break;
            }
            if (found) {
                Integer chapter = -1;
                int startLinks = -1;
                int startNum = -1;
                int startVers = -1;
                int index = text.indexOf(bookString);
                if (index > -1) {
                    startLinks = index;
                    String newSub = text.substring((index + bookString.length() - 1));
                    index = newSub.indexOf(" ");
                    if (index > -1) {
                        startNum = index + startLinks + bookString.length();
                        String num = newSub.substring(index + 1).trim();
                        index = num.indexOf(",");
                        if (index == -1) {
                            text = "";
                            found = false;
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
                                Optional<BibleTextUtils.BookLink> link = buildLink(utils, linkString);
                                System.out.println(linkString);
                                if (link.isPresent()) {
                                    bookLink.add(link.get());
                                }
                                found = true;
                                if (semicolon == -1) {
                                    text = text.substring(lastIndex);
                                } else {
                                    text = text.substring(semicolon).trim();
                                    index = text.indexOf(",");
                                    if (index > -1) {
                                        String nextNum = text.substring(0, index).trim();
                                        if (nextNum.matches("[0-9]+")) {
                                            text = bookString + " " + text;
                                        }
                                    }
                                }
                            } else {
                                text = text.substring(startVers);
                                found = false;
                            }
                        }
                    }
                }
            } else {
                text = "";
            }
        }

       return bookLink;
    }

    public static Tuple<Integer, Integer> cutString (String toCut, int startVers) {
        String lastPart = toCut.substring(startVers);
        int semiColonInd = lastPart.indexOf(";");
        int blankInd = lastPart.indexOf(" ");
        int bracketInd = lastPart.indexOf(")");
        int exclamationInd = lastPart.indexOf("!");
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
