package io.github.sevjet.essensedefence.field;

import com.jme3.export.*;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import io.github.sevjet.essensedefence.entity.Entity;
import io.github.sevjet.essensedefence.entity.building.Building;
import io.github.sevjet.essensedefence.entity.building.Fortress;
import io.github.sevjet.essensedefence.entity.building.Portal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.github.sevjet.essensedefence.util.Creator.gridXY;

public class Field extends Node {

    protected Cell[][] cells;
    protected Map<Class<? extends Entity>, Node> objects;
    protected Node grid;
    private int rows;
    private int cols;

    @SuppressWarnings("unused")
    public Field() {
    }

    public Field(int colNum, int rowNum) {
        rows = rowNum;
        cols = colNum;
        this.setName("field");
        objects = new HashMap<>();
        cells = new Cell[rowNum][];
        for (int i = 0; i < rowNum; i++) {
            cells[i] = new Cell[colNum];
            for (int j = 0; j < colNum; j++) {
                cells[i][j] = new Cell(i, j);
                addObject(cells[i][j]);
            }
        }
        gridOn();
    }

    protected boolean gridOn() {
        // FIXME: 09/05/2016 lineWidth don't save
        grid = gridXY(getRows() + 1, getCols() + 1, 1, ColorRGBA.Gray, 5f);
        grid.setLocalTranslation(-0.5f, -0.5f, 0);
        attachChild(grid);
        return true;
    }

//    public Field(Cell[][] cells) {
//        this.setName("field");
//        int rowNum = cells.length, colNum = cells[0].length;
//        objects = new HashMap<>();
//        this.cells = cells;
//        for (Cell[] row : cells) {
//            for (Cell cell : row) {
//                if (cell.getBuilding() != null) {
//                    build(cell.getBuilding());
//                }
//                cell.updater();
//                addObject(cell);
//            }
//        }
//        gridOn();
//    }

    public Cell getCell(int x, int y) {
        if (x < cells.length) {
            if (y < cells[x].length) {
                return cells[x][y];
            }
        }
        return null;
    }

    public int[][] getPassable() {
        int passable[][] = new int[getRows()][];
        for (int i = 0; i < getRows(); i++) {
            passable[i] = new int[getCols()];
            for (int j = 0; j < getCols(); j++) {
                passable[i][j] = (cells[i][j].isPassable() &&
                        (cells[i][j].getBuilding() == null ||
                                cells[i][j].getBuilding() instanceof Fortress) ||
                                cells[i][j].getBuilding() instanceof Portal) ? 0 : -1;
            }
        }
        return passable;
    }

    public void build(int x, int y, Building building) {
        for (int i = x; i < x + building.getSize().getWidth(); i++) {
            for (int j = y; j < y + building.getSize().getHeight(); j++) {
                cells[i][j].setBuilding(building);
            }
        }
        building.setX(x);
        building.setY(y);

        addObject(building);
    }

    public void build(Building building) {
        build(building.getX(), building.getY(), building);
    }

    public Node getObjects(Class<? extends Entity> clazz) {
        return objects.get(clazz);
    }

    public Cell getCell(Geometry geom) {
        int x, y;
        if (geom != null && geom.getParent().getParent() == this) {
            x = (int) geom.getLocalTranslation().getX();
            y = (int) geom.getLocalTranslation().getY();
            return getCell(x, y);
        }
        return null;
    }

    public boolean addObject(Entity object) {
        Node node = objects.get(object.getClass());
        if (node == null) {
            node = new Node();
            objects.put(object.getClass(), node);
            attachChild(node);
        }
        if (!node.hasChild(object.getGeometry())) {
            node.attachChild(object.getGeometry());
            return true;
        }
        return false;
    }

    public boolean removeObject(Entity object) {
        Node node = objects.get(object.getClass());
        if (node == null) {
            return false;
        }
        if (node.hasChild(object.getGeometry())) {
            if (object instanceof Building) {
                detachFromCells((Building) object);
//                ((Building) object).setHealth(-1);
            }
            node.detachChild(object.getGeometry());
            return true;
        }
        return false;
    }

    public boolean enoughPlaceFor(Cell cell, Building building) {
        for (int i = cell.getX(); i < cell.getX() + building.getSize().getWidth(); i++) {
            for (int j = cell.getY(); j < cell.getY() + building.getSize().getHeight(); j++) {
                if (i >= cells.length ||
                        j >= cells[i].length ||
                        cells[i][j].getBuilding() != null)
                    return false;
            }
        }
        return true;
    }

    protected void detachFromCells(Building building) {
        building.destroy();
        for (int i = building.getX(); i < building.getX() + building.getSize().getWidth(); i++) {
            for (int j = building.getY(); j < building.getY() + building.getSize().getHeight(); j++) {
                cells[i][j].free();
            }
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);

        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(getRows(), "rows", 1);
        capsule.write(getCols(), "cols", 1);
        capsule.write(cells, "cells", null);
//        capsule.writeSavableMap(objects, "objects", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);

        InputCapsule capsule = im.getCapsule(this);
        rows = capsule.readInt("rows", 1);
        cols = capsule.readInt("cols", 1);

        // @TODO remake
        Savable[][] data = capsule.readSavableArray2D("cells", null);
        cells = new Cell[getRows()][];
        for (int i = 0; i < getRows(); i++) {
            cells[i] = new Cell[getCols()];
            for (int j = 0; j < getCols(); j++) {
                cells[i][j] = (Cell) data[i][j];
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
