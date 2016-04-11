package com.eurexchange.clear.frontend;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 * @author carvcal
 */
public class HighLightText {

    private String word;
    private JTextArea textArea;
    private Color color;

    public HighLightText(JTextArea pTextArea, String pWord, Color pColor) {
        this.word = pWord;
        this.textArea = pTextArea;
        this.color = pColor;
        highlight(textArea, word, color);
    }

    private void highlight(JTextComponent textComponent, String word, Color color) {
        Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(color);
        removeHighlights(textComponent);

        try {
            Highlighter highlighter = textComponent.getHighlighter();
            Document doc = textComponent.getDocument();
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.toUpperCase().indexOf(word.toUpperCase(), pos)) >= 0) {
                highlighter.addHighlight(pos+7, pos + word.length() + 14, myHighlightPainter);
                pos += word.length();
            }
        } catch (BadLocationException s) {
        }

    }

    public void removeHighlights(JTextComponent textComp) {
        Highlighter highlighter = textComp.getHighlighter();
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight highlight : highlights) {
            if (highlight.getPainter() instanceof MyHighlightPainter) {
                highlighter.removeHighlight(highlight);
            }
        }
    }


    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {

        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
}
