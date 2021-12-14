/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

/**
 *
 * @author bmayr
 */
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class LinkFinder implements Runnable {

    private String url;
    private ILinkHandler linkHandler;
    /**
     * Used fot statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinder(String url, ILinkHandler handler) {
        this.url = url;
        this.linkHandler = handler;
    }

    @Override
    public void run() {
        getSimpleLinks(url);
    }

    private void getSimpleLinks(String url) {
        if (!linkHandler.visited(url)) {
            linkHandler.addVisited(url);
            Parser p;
            try {
                p = new Parser(url);
                NodeList nodeList = p.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                for (int i = 0; i < nodeList.size(); i++) {
                    LinkTag link = (LinkTag) nodeList.elementAt(i);
                    String linkString = link.getLink();
                    if (!linkString.isEmpty() && !linkHandler.visited(linkString)) {
                        linkHandler.queueLink(linkString);
                    }
                }
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (linkHandler.size() == 500) {
                long t1 = System.nanoTime();
                long elapsedTime = t1 - t0;
                System.out.println("elapsed Time: " + elapsedTime);
                System.exit(0);
            }
        }

    }
}
