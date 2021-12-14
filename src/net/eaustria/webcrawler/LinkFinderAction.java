/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eaustria.webcrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 *
 * @author bmayr
 */
// Recursive Action for forkJoinFramework from Java7
public class LinkFinderAction extends RecursiveAction {

    private String url;
    private ILinkHandler cr;
    /**
     * Used for statistics
     */
    private static final long t0 = System.nanoTime();

    public LinkFinderAction(String url, ILinkHandler cr) {
        this.url = url;
        this.cr = cr;
    }

    @Override
    public void compute() {
        if (!cr.visited(url)) {
            System.out.println(url);
            cr.addVisited(url);
            List<RecursiveAction> list = new ArrayList<>();
            Parser parser;
            try {
                parser = new Parser(url);

                NodeList nodeList = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                for (int i = 0; i < nodeList.size(); i++) {
                    LinkTag link = (LinkTag) nodeList.elementAt(i);
                    list.add(new LinkFinderAction(link.getLink(), cr));
                }
            } catch (ParserException e) {
                e.printStackTrace();
            }

            if (cr.size() == 500) {

                long t1 = System.nanoTime();
                long elapsedTime = t1 - t0;
                System.out.println("elapsed Time: " + elapsedTime);
                System.exit(0);
            }

            invokeAll(list);
        }
    }
}
