/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vap.codeparser;

import com.sun.source.tree.MethodTree;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.text.Document;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;
import org.vap.core.codegen.AbstractCodeParser;
import sun.reflect.generics.tree.TypeTree;

/**
 *
 * @author Oleg Bantysh
 */
@ServiceProvider(service = AbstractCodeParser.class)
public class AkkaJavaCodeParser implements AbstractCodeParser {

    private ArrayList<MethodTree> methods = new ArrayList<MethodTree>();
    private ArrayList<TypeTree> types = new ArrayList<TypeTree>();
    private String currentPath = new String();

    @Override
    public void init(String projectPath) {
        if (projectPath.equals(currentPath)) {
            return;
        }
        File root = new File(projectPath);
        for (File f : root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        })) {
            FileObject fo = FileUtil.toFileObject(f);
            JavaSource js = JavaSource.forFileObject(fo);
            if (js == null) {
            } else {

                try {
                    js.runUserActionTask(new Task<CompilationController>() {
                        public void run(CompilationController compilationController) throws Exception {
                            compilationController.toPhase(Phase.ELEMENTS_RESOLVED);

                            Document document = compilationController.getDocument();
                            if (document != null) {
                                //StatusDisplayer.getDefault().setStatusText("Hurray, the Java file is open!");
                            } else {
                                //StatusDisplayer.getDefault().setStatusText("The Java file is closed!");
                            }
                        }
                    }, true);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        }

    }

    @Override
    public ArrayList<String> getTypeNames() {
        return new ArrayList<String>();
    }

    @Override
    public ArrayList<MethodTree> getMethods() {
        return new ArrayList<MethodTree>();
    }

}
