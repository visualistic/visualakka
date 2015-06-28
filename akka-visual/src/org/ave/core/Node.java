package org.ave.core;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ave.core.exceptions.AVEInternaException;

/**
 *
 * @author Vladislav Larin
 */
public class Node {

    private final String name;
    private final String typeName;
    private final String defaultValue;
    
    private final boolean fixed;

    public Node(String name,
            String typeName, String defaultValue, boolean isFixed) {
        this.name = name;
        this.typeName = typeName;
        this.defaultValue = defaultValue == null ? "" : defaultValue;
        
        this.fixed = isFixed;
    }
   
    /**
     * @return the isFixed
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    public Object spawnDefaultValue() throws AVEInternaException {

        System.out.println("SPAWN DEF - " + name + " V - " + defaultValue);

        if (defaultValue == null || defaultValue.isEmpty()) {
            return null;
        }
        
        try {
            switch (typeName.toLowerCase()) {
                case "java.lang.string":
                    return defaultValue;
                case "java.lang.boolean":
                    return Boolean.valueOf(defaultValue);
                //case "char": return defaultValue.charAt(0);
                case "java.lang.integer":
                    return Integer.valueOf(defaultValue);
                case "java.lang.double":
                    return Double.valueOf(defaultValue);
            }
        } catch (Exception e) {
            return null;
        }

        try {
            Class type = Class.forName(typeName);
            return type.getConstructor(String.class).newInstance(defaultValue);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new AVEInternaException("Node type isn't found or invalid!");
        } catch (InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException |
                SecurityException ex) {

            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            throw new AVEInternaException("Failed to spawn default value! CAUSED BY:"
                    + ex.getMessage());
        }
    }


}
