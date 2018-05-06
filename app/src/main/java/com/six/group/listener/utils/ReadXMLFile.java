package com.six.group.listener.utils;

import android.util.Log;

import com.six.group.listener.data.conf.ArchivoXml;
import com.six.group.listener.data.json.Tarea;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReadXMLFile {
    private ArchivoXml datosXml;

    public ReadXMLFile(InputStream xml) {
        try {
            datosXml = new ArchivoXml();

            if (xml != null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xml);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("idUsuario");
                datosXml.setIdUsuario(nList.item(0).getTextContent());

                nList = doc.getElementsByTagName("Tarea");

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);

                    Log.d("\nCurrent Element :", nNode.getNodeName());

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;

                        Tarea tarea = new Tarea();
                        tarea.setKeyTarea(eElement.getElementsByTagName("idTarea").item(0).getTextContent());
                        tarea.setInstrucciones(eElement.getElementsByTagName("instrucciones").item(0).getTextContent());
                        tarea.setUrlInicio(eElement.getElementsByTagName("urlInicio").item(0).getTextContent());
                        tarea.setUrlFinal(eElement.getElementsByTagName("urlFinal").item(0).getTextContent());
                        tarea.setTiempo(eElement.getElementsByTagName("tiempo").item(0).getTextContent());
                        datosXml.addDato(tarea);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArchivoXml getDatosXml() {
        return datosXml;
    }


}
