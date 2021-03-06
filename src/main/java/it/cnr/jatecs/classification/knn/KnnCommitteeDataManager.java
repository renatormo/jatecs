/*
 * This file is part of JaTeCS.
 *
 * JaTeCS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JaTeCS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JaTeCS.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The software has been mainly developed by (in alphabetical order):
 * - Andrea Esuli (andrea.esuli@isti.cnr.it)
 * - Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 * - Alejandro Moreo Fernández (alejandro.moreo@isti.cnr.it)
 * Other past contributors were:
 * - Giacomo Berardi (giacomo.berardi@isti.cnr.it)
 */

package it.cnr.jatecs.classification.knn;

import it.cnr.jatecs.classification.ClassifierRange;
import it.cnr.jatecs.classification.interfaces.IClassifier;
import it.cnr.jatecs.classification.interfaces.IClassifierRuntimeCustomizer;
import it.cnr.jatecs.classification.interfaces.IDataManager;
import it.cnr.jatecs.classification.interfaces.ILearnerRuntimeCustomizer;
import it.cnr.jatecs.io.IStorageManager;
import it.cnr.jatecs.utils.Os;

import java.io.*;
import java.util.Hashtable;
import java.util.Iterator;

public class KnnCommitteeDataManager implements IDataManager {

    public IClassifier read(String modelDir) {
        throw new RuntimeException("Method read() not available");
    }

    public IClassifierRuntimeCustomizer readClassifierRuntimeConfiguration(
            String confDir) throws IOException {
        File f = new File(confDir + Os.pathSeparator() + "knn.db");
        DataInputStream is = new DataInputStream(new BufferedInputStream(
                new FileInputStream(f)));

        KnnCommitteeClassifierCustomizer c = new KnnCommitteeClassifierCustomizer();
        int num = is.readInt();
        c._ranges = new Hashtable<Short, ClassifierRange>(num);
        for (int i = 0; i < num; i++) {
            short catID = is.readShort();
            double border = is.readDouble();
            double maximum = is.readDouble();
            double minimum = is.readDouble();

            ClassifierRange cr = new ClassifierRange();
            cr.border = border;
            cr.maximum = maximum;
            cr.minimum = minimum;

            c._ranges.put(catID, cr);
        }

        is.close();

        return c;
    }

    public ILearnerRuntimeCustomizer readLearnerRuntimeConfiguration(
            String confDir) throws IOException {
        throw new RuntimeException(
                "Method readLearnerRuntimeConfiguration() not available");
    }

    public void write(String modelDir, IClassifier learningData)
            throws IOException {
        throw new IOException("Method write() not available");
    }

    public void writeClassifierRuntimeConfiguration(String confDir,
                                                    IClassifierRuntimeCustomizer customizer) throws IOException {
        KnnCommitteeClassifierCustomizer c = (KnnCommitteeClassifierCustomizer) customizer;

        File f = new File(confDir);
        if (!f.exists())
            f.mkdirs();

        String fname = confDir + Os.pathSeparator() + "knn.db";
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(fname)));

        os.writeInt(c._ranges.size());
        Iterator<Short> it = c._ranges.keySet().iterator();
        while (it.hasNext()) {
            short catID = it.next();
            ClassifierRange cr = c.getClassifierRange(catID);
            os.writeShort(catID);
            os.writeDouble(cr.border);
            os.writeDouble(cr.maximum);
            os.writeDouble(cr.minimum);
        }

        os.close();
    }

    public void writeLearnerRuntimeConfiguration(String confDir,
                                                 ILearnerRuntimeCustomizer customizer) throws IOException {
        throw new RuntimeException(
                "Method writeLearnerRuntimeConfiguration() not available");
    }

    @Override
    public void write(IStorageManager storageManager, String modelName,
                      IClassifier learningData) {
        throw new RuntimeException("Method write() not available");
    }

    @Override
    public IClassifier read(IStorageManager storageManager, String modelName) {
        throw new RuntimeException("Method read() not available");
    }

    @Override
    public void writeLearnerRuntimeConfiguration(
            IStorageManager storageManager, String modelName,
            ILearnerRuntimeCustomizer customizer) {
        throw new RuntimeException(
                "Method writeLearnerRuntimeConfiguration() not available");
    }

    @Override
    public ILearnerRuntimeCustomizer readLearnerRuntimeConfiguration(
            IStorageManager storageManager, String modelName) {
        throw new RuntimeException(
                "Method readLearnerRuntimeConfiguration() not available");
    }

    @Override
    public void writeClassifierRuntimeConfiguration(
            IStorageManager storageManager, String modelName,
            IClassifierRuntimeCustomizer customizer) {
        if (storageManager == null)
            throw new NullPointerException("The storage manager is 'null'");
        if (modelName == null || modelName.isEmpty())
            throw new IllegalArgumentException("The model name is invalid");
        if (customizer == null)
            throw new NullPointerException();
        if (!storageManager.isOpen())
            throw new IllegalStateException("The storage manager is not open");

        KnnCommitteeClassifierCustomizer c = (KnnCommitteeClassifierCustomizer) customizer;

        String fname = modelName + storageManager.getPathSeparator() + "knn.db";
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(
                storageManager.getOutputStreamForResource(fname)));

        try {
            try {
                os.writeInt(c._ranges.size());
                Iterator<Short> it = c._ranges.keySet().iterator();
                while (it.hasNext()) {
                    short catID = it.next();
                    ClassifierRange cr = c.getClassifierRange(catID);
                    os.writeShort(catID);
                    os.writeDouble(cr.border);
                    os.writeDouble(cr.maximum);
                    os.writeDouble(cr.minimum);
                }
            } finally {
                os.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Writing classifier runtime configuration", e);
        }
    }

    @Override
    public IClassifierRuntimeCustomizer readClassifierRuntimeConfiguration(
            IStorageManager storageManager, String modelName) {
        if (storageManager == null)
            throw new NullPointerException("The storage manager is 'null'");
        if (modelName == null || modelName.isEmpty())
            throw new IllegalArgumentException("The model name is invalid");
        if (!storageManager.isOpen())
            throw new IllegalStateException("The storage manager is not open");

        String fname = modelName + storageManager.getPathSeparator() + "knn.db";
        DataInputStream is = new DataInputStream(new BufferedInputStream(
                storageManager.getInputStreamForResource(fname)));

        try {
            try {
                KnnCommitteeClassifierCustomizer c = new KnnCommitteeClassifierCustomizer();
                int num = is.readInt();
                c._ranges = new Hashtable<Short, ClassifierRange>(num);
                for (int i = 0; i < num; i++) {
                    short catID = is.readShort();
                    double border = is.readDouble();
                    double maximum = is.readDouble();
                    double minimum = is.readDouble();

                    ClassifierRange cr = new ClassifierRange();
                    cr.border = border;
                    cr.maximum = maximum;
                    cr.minimum = minimum;

                    c._ranges.put(catID, cr);
                }

                return c;
            } finally {
                is.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Reading classifier runtime configuration", e);
        }
    }

}
