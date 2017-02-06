/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.svatos.nb.osgi.runner;

import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jiri.svatos
 */
public class ModulConfigrationTableModel extends DefaultTableModel {

    private final List<ModuleConfiguration> moduleConfigurations;
    //
    private final String[] names = new String[]{"enable", "name", "path"};

    public ModulConfigrationTableModel(List<ModuleConfiguration> module) {
        this.moduleConfigurations = module;
    }

    public List<ModuleConfiguration> getModuleConfigurations() {
        return moduleConfigurations;
    }

    @Override
    public Object getValueAt(int row, int column) {
        ModuleConfiguration mc = moduleConfigurations.get(row);
        switch (column) {
            case 0:
                return mc.isEnable();
            case 1:
                return mc.getBundleName();
            case 2:
                return mc.getPath();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public String getColumnName(int column) {
        if (names == null) {
            return "";
        }
        return names[column];
    }

    @Override
    public int getColumnCount() {
        if (names == null) {
            return 0;
        }
        return names.length;
    }

    @Override
    public int getRowCount() {
        return ((moduleConfigurations == null) ? 0 : moduleConfigurations.size());
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return ((columnIndex == 0) ? Boolean.class : String.class);
    }

    void add(ModuleConfiguration createFromJar) {
        moduleConfigurations.add(createFromJar);
        fireTableStructureChanged();
    }

}
