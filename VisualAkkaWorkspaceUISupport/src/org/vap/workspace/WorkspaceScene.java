/* 
 */
package org.vap.workspace;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotUndoException;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.ConnectProvider;
import org.netbeans.api.visual.action.ConnectorState;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.action.SelectProvider;
import org.netbeans.api.visual.action.TextFieldInplaceEditor;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.graph.GraphPinScene;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.awt.UndoRedo;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.vap.core.codegen.AbstractCodeParser;
import org.vap.core.filetype.WorkspaceFile;
import org.vap.core.model.macro.ConcreticisedMethod;
import org.vap.core.model.macro.Connection;
import org.vap.core.model.macro.Entry;
import org.vap.core.model.macro.Exit;
import org.vap.core.model.macro.State;
import org.vap.core.model.macro.StateSetter;
import org.vap.core.model.macro.UserCodeBlock;
import org.vap.core.model.macro.VFLayer;
import org.vap.core.model.macro.Workspace;
import org.vap.core.model.micro.Argument;
import org.vap.core.model.micro.Method;
import org.vap.core.model.micro.Result;
import org.vap.core.model.macro.WorkspaceObject;
import org.vap.core.model.micro.Module;
import org.vap.workspace.propertynodes.entryNode;
import org.vap.workspace.propertynodes.exitNode;
import org.vap.workspace.propertynodes.stsetterNode;
import org.vap.workspace.propertynodes.unitNode;
import org.vap.workspace.transferhandlers.EntryButton;
import org.vap.workspace.transferhandlers.ExitButton;
import org.vap.workspace.transferhandlers.SelfButton;
import org.vap.workspace.transferhandlers.UCBButton;
import org.vap.workspace.widgets.ConnectionStateDisplayerWidget;
import org.vap.workspace.widgets.EntryWidget;
import org.vap.workspace.widgets.ExitWidget;
import org.vap.workspace.widgets.SourcePinWidget;
import org.vap.workspace.widgets.StateSetterWidget;
import org.vap.workspace.widgets.TargetPinWidget;
import org.vap.workspace.widgets.TitleWidget;
import org.vap.workspace.widgets.UCBWidget;
import org.vap.workspace.widgets.UnitWidget;
import org.vap.workspace.widgets.WorkspaceConnectionWidget;
import org.vap.workspace.wizards.UsrCodeBlockWizardAction;

/**
 *
 * @author Oleg Bantysh
 */
public final class WorkspaceScene extends GraphPinScene<String, String, String> implements ExplorerManager.Provider {//, Savable {

    private LayerWidget backgroundLayer;
    private LayerWidget connectionLayer;
    private LayerWidget mainLayer;
    private WidgetAction editorAction = ActionFactory.createInplaceEditorAction(new LabelTextFieldEditor());

    /**
     *
     */
    public Workspace ws;
    private HashMap<Widget, Node> wnManager = new HashMap();
    private ExplorerManager em = new ExplorerManager();
    private JToolBar tb = new JToolBar();
    private UndoRedo.Manager manager = new UndoRedo.Manager();
    private WorkspaceFile file;
    private JButton delMethBtn;
    private Node selectedNode;
    private TopComponent rtc;

    /**
     *
     */
    public boolean isInitialized = false;

    /**
     *
     */
    public WSNavigatorPanel satteliteView;

    /**
     *
     * @param ws
     * @param file
     */
    public WorkspaceScene(Workspace ws, WorkspaceFile file) {
        this.file = file;
        backgroundLayer = new LayerWidget(this);
        addChild(backgroundLayer);
        mainLayer = new LayerWidget(this);
        addChild(mainLayer);
        connectionLayer = new LayerWidget(this);
        addChild(connectionLayer);
        this.ws = ws;
        load();

        MyDropTargetListener dtl = new MyDropTargetListener();
        if (this.getView() == null) {
            this.createView();
        }
        DropTarget dt = new DropTarget(this.getView(), dtl);
        dt.setDefaultActions(DnDConstants.ACTION_COPY);
        dt.setActive(true);
        buildToolbar();
        getActions().addAction(ActionFactory.createZoomAction());
        getActions().addAction(ActionFactory.createPanAction());
        getActions().addAction(ActionFactory.createWheelPanAction());
        getActions().addAction(ActionFactory.createRectangularSelectAction(this, backgroundLayer));

    }

    /**
     *
     */
    public void load() {
        removeAll();
        if (ws.getActiveLayer() == null) {
            ws.setDefault();
        }
        initGrids();
        initWorkspace();
        validate();
    }

    /**
     *
     */
    public void removeAll() {
        Collection<String> nodes = this.getNodes();
        String[] ns = nodes.toArray(new String[nodes.size()]);
        for (int i = 0; i < ns.length; i++) {
            this.removeNodeWithEdges(ns[i]);
        }
    }

    /**
     *
     */
    public void initGrids() {
        Image sourceImage = ImageUtilities.loadImage("org/vap/workspace/resources/paper_grid17.png"); // NOI18N
        int width = sourceImage.getWidth(null);
        int height = sourceImage.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(sourceImage, 0, 0, null);
        graphics.dispose();
        TexturePaint PAINT_BACKGROUND = new TexturePaint(image, new Rectangle(0, 0, width, height));
        setBackground(PAINT_BACKGROUND);
//        repaint();
//        revalidate(false);
        //validate();
    }

    /**
     *
     */
    public void initWorkspace() {
        mainLayer.removeChildren();
        connectionLayer.removeChildren();
        for (ConcreticisedMethod m : ws.getActiveLayer().units) {
            //REMOVE NEXT LINE IN ORDER TO NORMAL FUNCTIONING
            m.loadDefaultProperties(false);
            //-----------------------------------------------
            addNode(m.getCmID());
            for (Argument a : m.getRefMeth().getArguments()) {
                if (!a.isFixed()) {
                    String s = m.getCmID() + ";" + a.getName() + ";0";
                    addPin(m.getCmID(), s);
                }
            }
            for (Argument a : m.getRefMeth().getArguments()) {
                if (a.isFixed()) {
                    String s = m.getCmID() + ";" + a.getName() + ";0";
                    UnitWidget widget = (UnitWidget) findWidget(m.getCmID());
                    widget.AttachFixedField(m, s);
                    //addPin(m.getCmID(), s);
                }
            }
            for (Result r : m.getRefMeth().getResults()) {
                String s = m.getCmID() + ";" + r.getName() + ";1";
                addPin(m.getCmID(), s);
            }
        }

        for (Entry e : ws.getActiveLayer().entries) {
            addNode(e.getCmID());
            addPin(e.getCmID(), e.toString());
        }

        for (Exit e : ws.getActiveLayer().exits) {
            addNode(e.getCmID());
            addPin(e.getCmID(), e.toString());
        }

        for (StateSetter sts : ws.getActiveLayer().stSetters) {
            addNode(sts.getCmID());
            addPin(sts.getCmID(), sts.getCmID() + ";" + sts.getRefState().getName() + ";0");
        }

        for (Connection c : ws.getActiveLayer().getConnections()) {
            addEdge(c.toString());
        }
        for (Connection c : ws.getActiveLayer().getConnections()) {
            setEdgeSource(c.toString(), c.getSourceSignature());
            setEdgeTarget(c.toString(), c.getTargetSignature());
        }
        //mainLayer.bringToFront();
    }

    /**
     *
     * @return
     */
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }

//    @Override
//    public void save() throws IOException {
//        file.save();
//    }
    private class NodeWidgetSelectProvider implements SelectProvider {

        @Override
        public boolean isAimingAllowed(Widget widget, Point point, boolean bln) {
            return true;
        }

        @Override
        public boolean isSelectionAllowed(Widget widget, Point point, boolean bln) {
            return true;
        }

        @Override
        public void select(Widget widget, Point point, boolean bln) {
            Node n = wnManager.get(widget);
            TopComponent tct = TopComponent.getRegistry().getActivated();
            tct.setActivatedNodes(new Node[]{n});
        }

    }

    /**
     *
     * @param n
     * @return
     */
    @Override
    protected Widget attachNodeWidget(final String n) {
        ParentWithUndoMoveStrategyProvider provider = new ParentWithUndoMoveStrategyProvider();
        ConcreticisedMethod m = ws.getActiveLayer().getUnitByID(n);
        Entry en = ws.getActiveLayer().getEntryByName(n);
        Exit ex = ws.getActiveLayer().getExitByName(n);
        StateSetter sts = ws.getActiveLayer().getStSetByID(n);
        Widget widget = null;
        Node un = null;
        if (m != null) {
            if (m instanceof UserCodeBlock) {
                widget = new UCBWidget(this, (UserCodeBlock) m, provider);
            } else {
                widget = new UnitWidget(this, m, provider);
            }
            un = new unitNode(m, this);
            //widget.setPreferredLocation(new Point(m.getLoc().x * 17, m.getLoc().y * 17));
            widget.setPreferredLocation(new Point(m.getLoc().x, m.getLoc().y));
        } else if (en != null) {
            final EntryWidget ew = new EntryWidget(this, en, provider);
            widget = ew;
            un = new entryNode(en, this);
//            widget.setPreferredLocation(new Point(en.getLoc().x * 17, en.getLoc().y * 17));
            widget.setPreferredLocation(new Point(en.getLoc().x, en.getLoc().y));
            //widget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), provider));
        } else if (ex != null) {
            final ExitWidget ew = new ExitWidget(this, ex, provider);
            widget = ew;
            un = new exitNode(ex, this);
//            widget.setPreferredLocation(new Point(ex.getLoc().x * 17, ex.getLoc().y * 17));
            widget.setPreferredLocation(new Point(ex.getLoc().x, ex.getLoc().y));
            //widget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), provider));
        } else if (sts != null) {
            final StateSetterWidget stsw = new StateSetterWidget(this, sts);
            widget = stsw;
            un = new stsetterNode(sts, this);
//            widget.setPreferredLocation(new Point(sts.getLoc().x * 17, sts.getLoc().y * 17));
            widget.setPreferredLocation(new Point(sts.getLoc().x, sts.getLoc().y));
            widget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), provider));
        }
        //System.out.println("Adding move strat");

//        widget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createFreeMoveStrategy(), provider));
        //widget.getActions().addAction(ActionFactory.createMoveAction(ActionFactory.createSnapToGridMoveStrategy(17, 17), provider));
        widget.getActions().addAction(createObjectHoverAction());
        widget.getActions().addAction(ActionFactory.createSelectAction(new NodeWidgetSelectProvider()));
        if (en != null && en.getRefArg().isIsMainArg()) {
            widget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
                @Override
                public JPopupMenu getPopupMenu(final Widget widget, Point localLocation) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteMenu = new JMenuItem("Delete");
                    deleteMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String o = (String) findObject(widget);
                            removeNodeWithEdges(o);
                            ws.getActiveLayer().removeUnitWithConnections(o);
                            //XXX: Savable support
                            file.makeDirty();
                            System.out.println();
                        }
                    });
                    deleteMenu.setEnabled(false);
                    popup.add(deleteMenu);
                    return popup;
                }
            }));
        } else if (en != null || ex != null) {
            widget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
                @Override
                public JPopupMenu getPopupMenu(final Widget widget, Point localLocation) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteMenu = new JMenuItem("Delete");
                    deleteMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String o = (String) findObject(widget);
                            removeNodeWithEdges(o);
                            ws.getActiveLayer().removeUnitWithConnections(o);
                            //XXX: Savable support
                            file.makeDirty();
                            System.out.println();
                        }
                    });
                    if (n.equals("0")) {
                        deleteMenu.setEnabled(false);
                    }
                    popup.add(deleteMenu);
                    return popup;
                }
            }));
        } else {
            widget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
                @Override
                public JPopupMenu getPopupMenu(final Widget widget, Point localLocation) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteMenu = new JMenuItem("Delete");
                    deleteMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String o = (String) findObject(widget);
                            removeNodeWithEdges(o);
                            ws.getActiveLayer().removeUnitWithConnections(o);
                            //XXX: Savable support
                            file.makeDirty();
                            System.out.println();
                        }
                    });
                    if (n.equals("0")) {
                        deleteMenu.setEnabled(false);
                    }
                    popup.add(deleteMenu);
                    JMenu copytoMenu = new JMenu("Copy to method");
                    for (final String s : Arrays.asList(ws.getMethodNames())) {
                        JMenuItem methodMenu = new JMenuItem(s);
                        methodMenu.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String o = (String) findObject(widget);
                                ConcreticisedMethod m = ws.getActiveLayer().getUnitByID(o);
                                Entry en = ws.getActiveLayer().getEntryByName(o);
                                Exit ex = ws.getActiveLayer().getExitByName(o);
                                try {
                                    if (m != null) {
                                        ws.findMethod(s).units.add((ConcreticisedMethod) m.clone());
                                    } else if (en != null) {

                                        ws.findMethod(s).entries.add((Entry) en.clone());

                                    } else if (ex != null) {
                                        ws.findMethod(s).exits.add((Exit) ex.clone());
                                    }

                                    //XXX: Savable support
                                    file.makeDirty();
                                } catch (CloneNotSupportedException ex1) {
                                    Exceptions.printStackTrace(ex1);
                                }
                            }
                        });
                        copytoMenu.add(methodMenu);
                    }
                    popup.add(copytoMenu);
                    return popup;
                }
            }));
        }
        wnManager.put(widget, un);

        mainLayer.addChild(widget);

        validate();
        return widget;
    }

    /**
     *
     * @param e
     * @return
     */
    @Override
    protected Widget attachEdgeWidget(String e) {
        //ConnectionWidget widget = new VMDConnectionWidget(this, RouterFactory.createOrthogonalSearchRouter(mainLayer, connectionLayer));
        ConnectionWidget widget = new WorkspaceConnectionWidget(this);
        widget.setTargetAnchorShape(AnchorShape.NONE);
//        widget.setRouter(RouterFactory.createOrthogonalSearchRouter(mainLayer, connectionLayer));
        connectionLayer.addChild(widget);
        widget.setOpaque(false);
        final Connection conn = ws.getActiveLayer().getConnection(e);
        if (conn.relatedState != null) {
            LabelWidget stateLabel = new ConnectionStateDisplayerWidget(this, "[" + conn.relatedState.getName() + "]");
            stateLabel.setOpaque(false);
            //stateLabel.getActions ().addAction (ActionFactory.createMoveAction ());
            widget.addChild(stateLabel);
            widget.setConstraint(stateLabel, LayoutFactory.ConnectionWidgetLayoutAlignment.CENTER_RIGHT, 0.5f);
        }
        if (conn.extractedFieldName != null) {
            LabelWidget stateLabel = new ConnectionStateDisplayerWidget(this, "(" + ws.getActiveLayer()
                    .getConnection(e).extractedFieldName + ")");
            stateLabel.setOpaque(false);
            //stateLabel.getActions ().addAction (ActionFactory.createMoveAction ());
            widget.addChild(stateLabel);
            widget.setConstraint(stateLabel, LayoutFactory.ConnectionWidgetLayoutAlignment.CENTER_RIGHT, 0.05f);
        }
        WidgetAction connectionHoverAction = ActionFactory.createHoverAction(new TwoStateHoverProvider() {

            @Override
            public void unsetHovering(Widget widget) {
                ((WorkspaceConnectionWidget) widget).Unhower();
                widget.revalidate();
            }

            @Override
            public void setHovering(Widget widget) {
                ((WorkspaceConnectionWidget) widget).Hower();
                widget.revalidate();
            }

        });
        widget.getActions().addAction(connectionHoverAction);
        this.getActions().addAction(connectionHoverAction);
        widget.getActions().addAction(ActionFactory.createPopupMenuAction(new PopupMenuProvider() {
            @Override
            public JPopupMenu getPopupMenu(final Widget widget, Point localLocation) {
                JPopupMenu popup = new JPopupMenu();
                JMenuItem deleteMenu = new JMenuItem("Delete");
                deleteMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String o = (String) findObject(widget);
                        removeEdge(o);
                        ws.getActiveLayer().removeConnection(o);
                        //XXX: Savable support
                        file.makeDirty();
                        System.out.println();
                    }
                });
                popup.add(deleteMenu);
                JMenu copytoMenu = new JMenu("Set state");
                ArrayList<String> strings = new ArrayList<String>();
                for (State s : ws.getStates()) {
                    strings.add(s.getName());
                }
                for (final String s : strings) {
                    JRadioButtonMenuItem methodMenu = new JRadioButtonMenuItem(s);
                    methodMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String o = (String) findObject(widget);
                            Connection c = ws.getActiveLayer().getConnection(o);
                            c.relatedState = ws.getState(s);
                            load();
                        }
                    });
                    if (conn.relatedState != null) {
                        if (conn.relatedState.getName().equals(s)) {
                            methodMenu.setSelected(true);
                        }
                    }
                    copytoMenu.add(methodMenu);
                }
                copytoMenu.addSeparator();
                JRadioButtonMenuItem methodMenu = new JRadioButtonMenuItem("All states");
                methodMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String o = (String) findObject(widget);
                        Connection c = ws.getActiveLayer().getConnection(o);
                        c.relatedState = null;
                        load();
                    }
                });
                if (conn.relatedState == null) {
                    methodMenu.setSelected(true);
                }
                copytoMenu.add(methodMenu);
                popup.add(copytoMenu);
                JMenu extractfieldMenu = new JMenu("Extract field");

                AbstractCodeParser gen = (AbstractCodeParser) Lookup.getDefault().lookup(AbstractCodeParser.class);
                ArrayList<String> fields = gen.getTypeNames();
                //Temporary stub with use of singleton, not Lookup            
//                ArrayList<String> fields = AstSingleton.getInstance().getTypeNames();

//                for (State s : ws.getStates()) {
//                    fields.add(s.getName());
//                }
                for (final String s : fields) {
                    JRadioButtonMenuItem fieldMenu = new JRadioButtonMenuItem(s);
                    methodMenu.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String o = (String) findObject(widget);
                            Connection c = ws.getActiveLayer().getConnection(o);
                            c.extractedFieldName = s;
                            load();
                        }
                    });
                    if (conn.extractedFieldName != null) {
                        if (conn.extractedFieldName.equals(s)) {
                            fieldMenu.setSelected(true);
                        }
                    }
                    extractfieldMenu.add(fieldMenu);
                }
                extractfieldMenu.addSeparator();
                JMenuItem fieldNaNMenu = new JMenuItem("None");
                fieldNaNMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String o = (String) findObject(widget);
                        Connection c = ws.getActiveLayer().getConnection(o);
                        c.extractedFieldName = null;
                        load();
                    }
                });
                if (conn.extractedFieldName == null) {
                    fieldNaNMenu.setSelected(true);
                }
                extractfieldMenu.add(fieldNaNMenu);
                JMenuItem fieldCustomMenu = new JMenuItem("Custom");
                fieldCustomMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String o = (String) findObject(widget);
                        Connection c = ws.getActiveLayer().getConnection(o);
                        c.extractedFieldName = (String) JOptionPane.showInputDialog(
                                null,
                                "Input field name",
                                "Extract field",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                "Extract filed");
                        load();
                    }
                });
                extractfieldMenu.add(fieldCustomMenu);
                popup.add(extractfieldMenu);
                return popup;
            }
        }));
        return widget;
    }

    /**
     *
     * @param n
     * @param p
     * @return
     */
    @Override
    protected Widget attachPinWidget(String n, String p) {
        Widget widget = findWidget(n);
        Widget pw = null;
        if (widget instanceof UnitWidget) {
            pw = ((UnitWidget) widget).AttachPin(ws.getActiveLayer().getUnitByID(n), p);
        } else if (widget instanceof EntryWidget) {
            pw = ((EntryWidget) widget).InitPin(ws.getActiveLayer().getEntryByName(n));
        } else if (widget instanceof ExitWidget) {
            pw = ((ExitWidget) widget).InitPin(ws.getActiveLayer().getExitByName(n));
        } else if (widget instanceof StateSetterWidget) {
            pw = ((StateSetterWidget) widget).getTargetWidget();
        }
        pw.getActions().addAction(ActionFactory.createConnectAction(connectionLayer, new ConnectProvider() {
            public boolean isSourceWidget(Widget sourceWidget) {
                return sourceWidget instanceof SourcePinWidget && sourceWidget != null;
            }

            public ConnectorState isTargetWidget(Widget sourceWidget, Widget targetWidget) {
                if (targetWidget instanceof TargetPinWidget) {
                    return ConnectorState.ACCEPT;
                } else {
                    return ConnectorState.REJECT_AND_STOP;
                }
            }

            @Override
            public boolean hasCustomTargetWidgetResolver(Scene scene) {
                return false;
            }

            @Override
            public Widget resolveTargetWidget(Scene scene, Point sceneLocation) {
                return null;
            }

            @Override
            public void createConnection(Widget sourceWidget, Widget targetWidget) {
                SourcePinWidget s = (SourcePinWidget) sourceWidget;
                TargetPinWidget t = (TargetPinWidget) targetWidget;
                Connection c = new Connection();
                c.sourceCMID = s.CMID;
                c.sourcePinName = s.name;
                c.targetCMID = t.CMID;
                c.targetPinName = t.name;
                ws.getActiveLayer().connections.put(c.toString(), c);
                addEdge(c.toString());

                setEdgeSource(c.toString(), c.getSourceSignature());
                setEdgeTarget(c.toString(), c.getTargetSignature());

                //XXX: Savable support
                file.makeDirty();
            }
        }));
        return pw;
    }

    /**
     *
     * @param edge
     * @param oldPin
     * @param pin
     */
    @Override
    protected void attachEdgeSourceAnchor(String edge, String oldPin, String pin) {
        ConnectionWidget c = (ConnectionWidget) findWidget(edge);
        Widget widget = findWidget(pin);
        Anchor a = AnchorFactory.createRectangularAnchor(widget);
        c.setSourceAnchor(a);
    }

    /**
     *
     * @param edge
     * @param oldPin
     * @param pin
     */
    @Override
    protected void attachEdgeTargetAnchor(String edge, String oldPin, String pin) {
        ConnectionWidget c = (ConnectionWidget) findWidget(edge);
        Widget widget = findWidget(pin);
        Anchor a = AnchorFactory.createRectangularAnchor(widget);
        c.setTargetAnchor(a);
    }

    private class LabelTextFieldEditor implements TextFieldInplaceEditor {

        public boolean isEnabled(Widget widget) {
            return true;
        }

        public String getText(Widget widget) {
            return ((LabelWidget) widget).getLabel();
        }

        public void setText(Widget widget, String text) {
            ((LabelWidget) widget).setLabel(text);
        }

    }

    /**
     *
     */
    public class MyDropTargetListener implements DropTargetListener {

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dtde) {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
//            if (dtde.isDataFlavorSupported(ConcreticisedMethod.DATA_FLAVOR)) {
            try {
                Object transData = dtde.getTransferable().getTransferData(ConcreticisedMethod.DATA_FLAVOR);
                if (transData instanceof ConcreticisedMethod) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    ConcreticisedMethod m = (ConcreticisedMethod) dtde.getTransferable().
                            getTransferData(ConcreticisedMethod.DATA_FLAVOR);
                    m.loadDefaultProperties(true);
                    m.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                    ws.getActiveLayer().units.add(m);
                    WorkspaceScene.this.load();

                    //XXX: Savable support
                    file.makeDirty();
                }
                if (transData instanceof EntryButton) {
                    Entry e = new Entry();
                    e.setCmID("" + ConcreticisedMethod.getCurrIDInc());
                    e.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                    e.setRefArg(new Argument());
                    e.getRefArg().setName("New entry");
                    e.getRefArg().setType("String");
                    e.getRefArg().setIsMainArg(false);
                    ws.getActiveLayer().entries.add(e);
                    WorkspaceScene.this.load();

                    //XXX: Savable support
                    file.makeDirty();
                }

                if (transData instanceof SelfButton) {
                    Module mod = ws.formModule();
                    ConcreticisedMethod m = ConcreticisedMethod.formCM(mod.
                            getMethodByName(ws.getActiveLayer().methodName), mod.toString());
                    m.setSelType(ConcreticisedMethod.SelectorType.Self);
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    m.loadDefaultProperties(true);
                    m.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                    ws.getActiveLayer().units.add(m);
                    WorkspaceScene.this.load();

                    //XXX: Savable support
                    file.makeDirty();
                }

                if (transData instanceof ExitButton) {
                    Exit e = new Exit();
                    e.setRefRes(new Result());
                    e.setCmID("" + ConcreticisedMethod.getCurrIDInc());
                    e.getRefRes().setName("New exit");
                    e.getRefRes().setType("String");
                    e.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                    ws.getActiveLayer().exits.add(e);
                    WorkspaceScene.this.load();

                    //XXX: Savable support
                    file.makeDirty();
                }
                if (transData instanceof UCBButton) {
                    Method rm = UsrCodeBlockWizardAction.requestForUCB();
                    if (rm != null) {
                        UserCodeBlock m = UserCodeBlock.formUCB(rm);
                        dtde.acceptDrop(DnDConstants.ACTION_COPY);
                        m.loadDefaultProperties(true);
                        m.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                        ws.getActiveLayer().units.add(m);
                        WorkspaceScene.this.load();

                        //XXX: Savable support
                        file.makeDirty();
                    }
                }
                if (transData instanceof StateSetter) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    StateSetter sts = (StateSetter) dtde.getTransferable().
                            getTransferData(ConcreticisedMethod.DATA_FLAVOR);
                    sts.setLoc(new org.vap.core.model.micro.Point(dtde.getLocation()));
                    ws.getActiveLayer().stSetters.add(sts);
                    WorkspaceScene.this.load();

                    //XXX: Savable support
                    file.makeDirty();

                }

            } catch (UnsupportedFlavorException ufe) {
                dtde.rejectDrop();
                dtde.dropComplete(true);
            } catch (IOException ioe) {
                dtde.rejectDrop();
                dtde.dropComplete(false);
            }
        }

    }

    /**
     *
     * @return
     */
    public JToolBar getToolbar() {
        return tb;
    }

    /**
     *
     * @return
     */
    public UndoRedo getUndoRedo() {
        return manager;

    }

    /**
     *
     */
    public final class ParentWithUndoMoveStrategyProvider implements MoveProvider {

        private Deque<Point> originalLocs = new ArrayDeque<Point>();
        private Deque<Point> suggestedLocs = new ArrayDeque<Point>();

        /**
         *
         * @param widget
         */
        @Override
        public void movementStarted(Widget widget) {
            if (widget instanceof TitleWidget) {
                widget = ((TitleWidget) widget).parent;
            }
            originalLocs.push(ActionFactory.createDefaultMoveProvider().getOriginalLocation(widget));
            suggestedLocs.clear();
            manager.addEdit(new MyAbstractUndoableEdit(widget));
        }

        /**
         *
         * @param widget
         */
        @Override
        public void movementFinished(Widget widget) {
            if (widget instanceof TitleWidget) {
                widget = ((TitleWidget) widget).parent;
            }
            String n = (String) findObject(widget);
            WorkspaceObject obj = ws.getActiveLayer().getObjectById(n);
            if (obj != null) {
                obj.setLoc(new org.vap.core.model.micro.Point(widget.getLocation()));
            }

            MyAbstractUndoableEdit myAbstractUndoableEdit = new MyAbstractUndoableEdit(widget);
            manager.undoableEditHappened(new UndoableEditEvent(widget, myAbstractUndoableEdit));

            //XXX: Savable support
            file.makeDirty();
        }

        class MyAbstractUndoableEdit extends AbstractUndoableEdit {

            private final Widget widget;

            private MyAbstractUndoableEdit(Widget widget) {
                this.widget = widget;
            }

            @Override
            public boolean canRedo() {
                return !suggestedLocs.isEmpty();
            }

            @Override
            public boolean canUndo() {
                return !originalLocs.isEmpty();
            }

            @Override
            public void undo() throws CannotUndoException {
                Point p = originalLocs.pop();

                suggestedLocs.push(widget.getLocation());
                widget.setPreferredLocation(p);
                getScene().validate();
            }

            @Override
            public void redo() throws CannotUndoException {
                Point p = suggestedLocs.pop();
                originalLocs.push(widget.getLocation());
                widget.setPreferredLocation(p);
                getScene().validate();
            }

        }

        /**
         *
         * @param widget
         * @return
         */
        @Override
        public Point getOriginalLocation(Widget widget) {
            return ActionFactory.createDefaultMoveProvider().getOriginalLocation(widget);
        }

        /**
         *
         * @param widget
         * @param location
         */
        @Override
        public void setNewLocation(Widget widget, Point location) {
            ActionFactory.createDefaultMoveProvider().setNewLocation(widget, location);
        }

    }

    /**
     *
     * @return
     */
    public Node getSelectedNode() {
        return selectedNode;
    }

    /**
     *
     * @param rtc
     */
    public void setRelatedTC(TopComponent rtc) {
        this.rtc = rtc;
    }

    /**
     *
     * @param em
     */
    public void setEM(ExplorerManager em) {
        this.em = em;
    }

    /**
     *
     */
    public void buildToolbar() {
        tb.setFloatable(false);
        JSeparator toolBarSeparator = new JToolBar.Separator();
        tb.add(toolBarSeparator);
        JLabel currMeth = new JLabel("Current method:");

        tb.add(currMeth);
        final JComboBox combo = new JComboBox(ws.getMethodNames());
        combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAll();
                ws.setActiveLayer((String) ((JComboBox) e.getSource()).getSelectedItem());
                initWorkspace();
                validate();

                //XXX: Savable support
                file.makeDirty();
            }
        });
        tb.add(combo);
        JButton mnb = new JButton("New method");
        mnb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) JOptionPane.showInputDialog(
                        null,
                        "Input method name",
                        "New method",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "New method");
                if (s != null && !s.isEmpty()) {
                    combo.addItem(s);
                    combo.setSelectedItem(s);
                    VFLayer layer = new VFLayer();
                    layer.methodName = s;
                    ws.getLayers().add(layer);
                    ws.setActiveLayer(s);
                    load();
                    validate();
                    Entry en = new Entry();
                    en.setCmID("" + ConcreticisedMethod.getCurrIDInc());
                    en.setLoc(new org.vap.core.model.micro.Point(1, 1));
                    en.setRefArg(new Argument());
                    en.getRefArg().setName(s);
                    en.getRefArg().setType("String");
                    en.getRefArg().setIsMainArg(true);
                    ws.getActiveLayer().entries.add(en);
                    addNode(en.getCmID());
                    addPin(en.getCmID(), e.toString());
                    delMethBtn.setEnabled(true);
                    //XXX: Savable support
                    file.makeDirty();
                }
            }
        });
        tb.add(mnb);
        delMethBtn = new JButton("Delete method");
        delMethBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VFLayer l = ws.getActiveLayer();
                ws.removeLayer(l);
                if (ws.getLayers().size() == 1) {
                    delMethBtn.setEnabled(false);
                }
                combo.removeItem(l.methodName);
                combo.setSelectedIndex(0);
                load();
                //XXX: Savable support
                file.makeDirty();
            }
        });
        if (ws.getLayers().size() == 1) {
            delMethBtn.setEnabled(false);
        }
        tb.add(delMethBtn);
    }
}
