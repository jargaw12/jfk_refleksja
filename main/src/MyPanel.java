import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class MyPanel extends JPanel {
    private DefaultListModel<String> model;
    private JLabel labelResult;
    private JLabel labelArguments;
    private JTextField textFieldArguments;
    private JTextArea jTextArea;
    private JButton btnExec;
    private URLClassLoader cl;
    private Class<?> loadClass;


    public MyPanel() {
        setLayout(new GridBagLayout());
        initGUI();
    }

    private AbstractAction open = new AbstractAction("Otwórz") {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles;
                FileFilter filter=new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".jar");
                    }
                };

                if (chooser.getSelectedFile().isDirectory()) {
                    selectedFiles = chooser.getSelectedFile().listFiles(filter);
                } else selectedFiles = chooser.getSelectedFiles();
                dzialaj(selectedFiles);
            }
        }
    };

    private void dzialaj(File[] selectedFiles) {
        model.clear();
        JarFile jarFile = null;
        try {
            URL[] urls = new URL[selectedFiles.length];
            for (int i = 0; i < selectedFiles.length; i++)
                urls[i] = new URL("jar:file:" + selectedFiles[i].getAbsolutePath() + "!/");
            cl = URLClassLoader.newInstance(urls);
            for (File f : selectedFiles) {
                jarFile = new JarFile(f.getAbsolutePath());
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    try {
                        loadClass = cl.loadClass(className);
                        if (!loadClass.isAnnotationPresent(Description.class))
                            continue;

                        if (!ICallable.class.isAssignableFrom(loadClass))
                            throw new Exception("Class " + className + " does not implement the contract.");
                        else
                            model.addElement(loadClass.getName());
                    } catch (ClassNotFoundException exp) {
                        continue;
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } catch (IOException exp) {
        } finally {
            if (null != jarFile)
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private AbstractAction exec = new AbstractAction("Wykonaj") {
        @Override
        public void actionPerformed(ActionEvent e) {
            ICallable callable;
            String[] tokens = String.valueOf(textFieldArguments.getText()).split(",");
            int a1 = 0;
            int a2 = 0;
            try {
                a1 = Integer.parseInt(tokens[0]);
                a2 = Integer.parseInt(tokens[1]);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Zły format wprowadzonych argumentów");
            }

            try {
                callable = (ICallable) loadClass.newInstance();
                labelResult.setText("Wynik: " + callable.Call(a1, a2));
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    };

    private void initGUI() {
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        JButton btnOpen;btnOpen = new JButton(open);
        add(btnOpen, c);

        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.gridheight = 6;
        model = new DefaultListModel();
        JList<String> list;list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                try {
                    loadClass = cl.loadClass(list.getSelectedValue());
                } catch (ClassNotFoundException e1) {
                }
                Description description = (Description) loadClass.getAnnotation(Description.class);
                jTextArea.setText("Opis: " + description.description());
                labelArguments.setText("Podaj arumenty w postaci: liczba1,liczba2");
                btnExec.setEnabled(true);
            }
        });
        add(list, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 2;
        c.gridheight = 1;
        jTextArea = new JTextArea("Opis: ");
        jTextArea.setFont(new Font("TimesRoman", Font.PLAIN, 12));
        jTextArea.setColumns(10);
        jTextArea.setRows(10);
        jTextArea.setLineWrap(true);
        jTextArea.setWrapStyleWord(true);
        JScrollPane jScrollPane = new JScrollPane();
        jTextArea.setEditable(false);
        jTextArea.setSize(20, 54);
        jScrollPane.setViewportView(jTextArea);
        add(jScrollPane, c);

        c.gridy = 3;
        labelArguments = new JLabel();
        add(labelArguments, c);

        c.gridy = 4;
        textFieldArguments = new JTextField();
        add(textFieldArguments, c);

        c.gridy = 5;
        btnExec = new JButton(exec);
        btnExec.setEnabled(false);
        add(btnExec, c);

        c.gridy = 6;
        labelResult = new JLabel("Wynik: ");
        add(labelResult, c);
    }
}
