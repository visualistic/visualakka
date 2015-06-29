/* 
 */
package org.vap.workspace.widgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.MoveStrategy;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.vmd.VMDFactory;
import org.netbeans.api.visual.widget.ComponentWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.SeparatorWidget;
import org.netbeans.api.visual.widget.Widget;
import org.vap.core.model.macro.ConcreticisedMethod;
import org.vap.core.model.macro.UserCodeBlock;
import org.vap.core.model.micro.Argument;
import org.vap.core.model.micro.Method;
import org.vap.core.model.micro.Pin;
import org.vap.core.model.micro.Result;
import org.vap.workspace.WorkspaceScene;
import org.vap.workspace.wizards.UsrCodeBlockWizardAction;

/**
 *
 * @author Oleg Bantysh
 */
public class UCBWidget extends UnitWidget{
    
    private UserCodeBlock ucb;

    public UCBWidget(Scene scene, UserCodeBlock m, MoveProvider provider) {
        super(scene, m, provider);
        ucb = m;
        setOpaque(false);
        setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 4)); // use vertical layout

        TitleWidget methodName = new TitleWidget(this.getScene(), this, m.getMethodName());
        methodName.setAlignment(LabelWidget.Alignment.CENTER);
        methodName.setBorder(BorderFactory.createEmptyBorder(5, 2));
        //addChild(methodName);
        
        Widget titleField = new Widget(this.getScene());
        titleField.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 1));
        JButton editBtn = new JButton("Edit");
        JButton emptyBtn = new JButton("Edit");
        emptyBtn.setVisible(false);
        editBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Method meth = UsrCodeBlockWizardAction.requestForModifiedUCB(ucb.getRefMeth());
                ucb.UpdateName();
                ((WorkspaceScene)UCBWidget.this.getScene()).load();
            }
        });
        ComponentWidget editBtnWg = new ComponentWidget(scene, editBtn);
        ComponentWidget editBtnWgEmpty = new ComponentWidget(scene, emptyBtn);
//        editBtnWgEmpty.setVisible(false);
//        editBtnWgEmpty.setEnabled(false);
        titleField.addChild(editBtnWgEmpty);
        titleField.addChild(methodName);
        titleField.addChild(editBtnWg);
        addChild(titleField);   
        
        methodName.getActions().addAction(ActionFactory.createMoveAction(new MoveStrategy() {

            @Override
            public Point locationSuggested(Widget widget, Point point, Point point1) {

                Point pdif;
                if (currentLoc == null) {
                    pdif = new Point(point1.x - point.x, point1.y - point.y);
                } else {
                    pdif = new Point(point1.x - currentLoc.x, point1.y - currentLoc.y);
                }

                Point prevLoc = UCBWidget.this.getPreferredLocation();
                Point suggLoc = new Point(prevLoc.x + pdif.x,
                        prevLoc.y + pdif.y);
                UCBWidget.this.setPreferredLocation(suggLoc);
                currentLoc = point1;
                return point1;
            }
//        }, ActionFactory.createDefaultMoveProvider()));
        }, provider));
        
        Widget pinsSeparator = new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL);
        addChild(pinsSeparator);
        addChild(new LabelWidget(this.getScene(), m.getActorscope().name()));
        if (m.router != null && m.getActorscope()==ConcreticisedMethod.InstancingType.Routed) {
            Widget routerWidget = new Widget(scene);
            routerWidget.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 4));
            routerWidget.addChild(new LabelWidget(this.getScene(), m.router.getLogic().name()));
            if (m.router.isIsStretched()) {
                routerWidget.addChild(new LabelWidget(this.getScene(), "(" + m.router.getMinRoutes() + " .. "
                        + m.router.getMaxRoutes() + ")"));
            } else {
                routerWidget.addChild(new LabelWidget(this.getScene(), "(" + m.router.getMinRoutes() + ")"));
            }
            addChild(routerWidget);
            Widget pinsSeparator1 = new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL);
            addChild(pinsSeparator1);
        } 
//        else {
//            LabelWidget noRouter = new LabelWidget(this.getScene(), "(no router)");
//            noRouter.setAlignment(LabelWidget.Alignment.CENTER);
//            addChild(noRouter);
//        }
        
        Widget methodField = new Widget(this.getScene());
        methodField.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.JUSTIFY, 4)); // use vertical layout
        parameterField = new Widget(this.getScene());
        parameterField.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 2));
        exitsField = new Widget(this.getScene());
        exitsField.setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.RIGHT_BOTTOM, 2));
        parameterField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        exitsField.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        methodField.addChild(parameterField);
        methodField.addChild(new SeparatorWidget(scene, SeparatorWidget.Orientation.HORIZONTAL));
        methodField.addChild(exitsField);
        methodField.setBorder(BorderFactory.createEmptyBorder(5));
        addChild(methodField);
        setBorder(VMDFactory.createVMDNodeBorder(new Color(63, 67, 173), 2, new Color(199, 215, 224),
                new Color(199, 215, 224), new Color(199, 215, 224), new Color(199, 215, 224),
                new Color(199, 215, 224)));
        setToolTipText("ID " + m.getCmID() + "  Package " + m.getModuleID());
    }

    public Widget AttachPin(UserCodeBlock m, String pin) {
        Widget w = new Widget(this.getScene());
        String[] pe = pin.split(";");
        Pin p = m.getRefMeth().getParameterByName(pe[1]);
        Widget im = null;

        if (p.isEntry()) {
            w.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 1));
            im = new TargetPinWidget(this.getScene(), m.getCmID(), p.getName());
            w.addChild(im); // add image sub-widget
            LabelWidget name = new LabelWidget(this.getScene(), p.getName());
            name.setFont(new Font("Arial", Font.BOLD, 13));
            w.addChild(name); // add label sub-widget
            String s = m.getProperties().get(p.getName());
            LabelWidget value = null;
//            if (s != null) {
//                if (s.equals("")) {
//                    value = new LabelWidget(this.getScene(), ": (No value)"
//                            + " (" + ((Argument) p).getType() + ")");
//                } else {
//                    value = new LabelWidget(this.getScene(), ": " + s
//                            + " (" + ((Argument) p).getType() + ")");
//                }
//
//            } else {
//                value = new LabelWidget(this.getScene(), ": (No value)"
//                        + " (" + ((Argument) p).getType() + ")");
//            }
                value = new LabelWidget(this.getScene(), " (" + ((Argument) p).getType() + ")");
            value.setForeground(Color.gray);
            w.addChild(value);
            parameterField.addChild(w);
        } else {
            im = new SourcePinWidget(this.getScene(), m.getCmID(), p.getName());
            w.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.RIGHT_BOTTOM, 1));
            w.addChild(new LabelWidget(this.getScene(), p.getName())); // add label sub-widget
            Widget type = new LabelWidget(this.getScene(), ": (" + ((Result) p).getType() + ")");
            type.setForeground(Color.GRAY);
            w.addChild(type);
            w.addChild(im); // add image sub-widget
            exitsField.addChild(w);
        }
        return im;
    }

    public void AttachFixedField(UserCodeBlock m, String pin) {
        Widget w = new Widget(this.getScene());
        String[] pe = pin.split(";");
        Pin p = m.getRefMeth().getParameterByName(pe[1]);
        w.setLayout(LayoutFactory.createHorizontalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 1));
        LabelWidget name = new LabelWidget(this.getScene(), p.getName());
        name.setFont(new Font("Arial", Font.BOLD, 13));
        w.addChild(name); // add label sub-widget
        String s = m.getProperties().get(p.getName());
        LabelWidget value = null;
        if (s != null) {
            if (s.equals("")) {
                value = new LabelWidget(this.getScene(), ": (No value)"
                        + " (" + ((Argument) p).getType() + ")");
            } else {
                value = new LabelWidget(this.getScene(), ": " + s
                        + " (" + ((Argument) p).getType() + ")");
            }

        } else {
            value = new LabelWidget(this.getScene(), ": (No value)"
                    + " (" + ((Argument) p).getType() + ")");
        }
        value.setForeground(Color.gray);
        w.addChild(value);
        parameterField.addChild(w);
    }
    
    private void UpdateAfterEdit(){
        
    }
    
}
