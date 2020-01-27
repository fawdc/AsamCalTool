/*
 * Creation : 27 janv. 2020
 */
package gui;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.Segment;

public class TextSearchTest {

    private static class Search implements Callable<List<Integer>> {

        private Document document;
        List<Integer> dataOffsets;
        String searchString;

        public Search(Document document, String searchString) {
            this.document = document;
            this.searchString = searchString;
        }

        @Override
        public List<Integer> call() throws Exception {
            search();

            return dataOffsets;
        }

        @SuppressWarnings("boxing")
        private void search() {

            List<Integer> lineOffsets = new ArrayList<Integer>();
            dataOffsets = new ArrayList<Integer>();
            Element element = document.getDefaultRootElement();
            int elementCount = element.getElementCount();

            for (int i = 0; i < elementCount; i++) {
                lineOffsets.add(element.getElement(i).getStartOffset());
            }
            lineOffsets.add(element.getElement(element.getElementCount() - 1).getEndOffset());

            int count = 0;
            int lsOffset;
            int leOffset;

            while (count < (lineOffsets.size() - 1)) {

                lsOffset = lineOffsets.get(count);
                leOffset = lineOffsets.get(count + 1);
                count++;
                Segment seg = new Segment();

                try {
                    document.getText(lsOffset, leOffset - lsOffset, seg);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

                String line = seg.toString();
                int mark = 0;

                while ((mark = line.indexOf(searchString, mark)) > -1) {
                    dataOffsets.add(lsOffset + mark);
                    mark += searchString.length();
                }
            }
        }

        public String getSearchString() {
            return searchString;
        }

    }

    private static class TextSearchPanel extends JPanel implements ActionListener {

        private static final long serialVersionUID = 1L;

        private String text = "This little line had some data,\n" + "And this little line had none.\n" + "Chorus:\n" + "data data data data";

        JTextField textField;
        JTextArea textArea;

        public TextSearchPanel() {
            super(new GridBagLayout());

            textField = new JTextField(20);
            textArea = new JTextArea(5, 20);

            textField.addActionListener(this);
            textField.setText("data");
            textArea.setEditable(true);
            textArea.setText(text);
            JScrollPane scrollPane = new JScrollPane(textArea);

            GridBagConstraints c = new GridBagConstraints();
            c.gridwidth = GridBagConstraints.REMAINDER;

            c.fill = GridBagConstraints.HORIZONTAL;
            add(textField, c);

            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.weighty = 1.0;
            add(scrollPane, c);
        }

        @SuppressWarnings("boxing")
        @Override
        public void actionPerformed(ActionEvent event) {

            Cursor startCursor = textArea.getCursor();
            Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            Highlighter highlighter = textArea.getHighlighter();
            String searchText = textField.getText();
            Search search = new Search(textArea.getDocument(), searchText);

            textArea.setEditable(false);
            textArea.setCursor(waitCursor);
            highlighter.removeAllHighlights();

            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<List<Integer>> offsets = service.submit(search);

            try {
                for (Integer start : offsets.get()) {
                    highlighter.addHighlight(start, start + searchText.length(), DefaultHighlighter.DefaultPainter);
                }
            } catch (Exception e) {
            }

            textArea.setEditable(true);
            textArea.setCursor(startCursor);
        }
    }

    private static void createAndShowGUI() {

        JFrame frame = new JFrame("TextSearchTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new TextSearchPanel());
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}