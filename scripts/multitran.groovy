import com.sun.glass.events.KeyEvent
import org.omegat.core.Core
import org.omegat.core.CoreEvents
import org.omegat.core.events.IEditorEventListener
import org.omegat.gui.editor.IPopupMenuConstructor
import org.omegat.gui.editor.SegmentBuilder
import org.omegat.gui.main.MainWindow

import javax.swing.*
import javax.swing.text.JTextComponent
import java.awt.event.ActionEvent

def KEY = "MULTITRAN"
def TITLE = "Multitran"
def DOMAIN = "multitran.ru"
def PATH = "/c/m.exe"

def pane = BrowserPlugin.getPane(KEY, TITLE, DOMAIN)

String caretWord = null
CoreEvents.registerEditorEventListener(new IEditorEventListener() {
    @Override
    void onNewWord(String s) {
        caretWord = s
    }
})
Action action = new AbstractAction() {
    @Override
    void actionPerformed(ActionEvent e) {
        String s = Core.getEditor().getSelectedText()
        if (s == null || s.isEmpty()) {
            s = caretWord
        }
        String blackchars = "( |<|>)"
        String stripRegex = "^${blackchars}+|${blackchars}+\$"
        s = s.trim()
        s = s.replaceAll(stripRegex,"")
        if (s.isEmpty()) return
        url = "http://${DOMAIN}${PATH}?"
        url += "s=${URLEncoder.encode(s, "UTF-8")}"
        pane.getBrowser().loadURL(url)
    }
}

Core.getEditor().registerPopupMenuConstructors(1000, new IPopupMenuConstructor() {
    @Override
    public void addItems(JPopupMenu menu, JTextComponent comp, int mousepos, boolean isInActiveEntry,
                         boolean isInActiveTranslation, final SegmentBuilder sb) {
        if (isInActiveEntry) {
            menu.addSeparator()
            JMenuItem menuItem = menu.add("Lookup in Multitran")
            menuItem.addActionListener(action)
        }
    }
})
MainWindow mainWindow = (MainWindow) Core.getMainWindow()
int COMMAND_MASK = System.getProperty("os.name").contains("OS X") ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK
KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK + COMMAND_MASK)
mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keystroke, "lookupInMultitran")
mainWindow.getRootPane().getActionMap().put("lookupInMultitran", action)