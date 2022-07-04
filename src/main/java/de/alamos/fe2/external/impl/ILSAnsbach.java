package de.alamos.fe2.external.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.alamos.fe2.external.ExtractorObject;
import de.alamos.fe2.external.enums.EAlarmDataEntries;
import de.alamos.splitting.api.AbstractAlarmExtractorV2;

public class ILSAnsbach extends AbstractAlarmExtractorV2 {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ExtractorObject extractFromMap(Map<String, String> map) {
		// Rückgabe Objekt
		ExtractorObject extractorObject = new ExtractorObject();
		/*
		 * In dieser Map können alle Parameter gesetzt werden, die über eine normale Zerlegung auch gesetzt werden
		 * können
		 */
		Map<String, String> extractedParams = new HashMap<>();

		/*
		 * Beispiel 1
		 */
		// Holen eines Wertes, hier das Stichwort
		String stichwort = map.get(EAlarmDataEntries.KEYWORD.getKey());

		// Zerlegen des Stichwortes am Trennzeichen "/"
		String[] parameters = stichwort.split("/");

		// Stichwort steht an erster Stelle, daher [0]
		String keyword = parameters[0];
		// Wert für Stichwort wird in FE2 ersetzt
		extractedParams.put(EAlarmDataEntries.KEYWORD.getKey(), keyword);

		// Stichwortbeschreibung steht an zweiter Stelle, daher [1]
		String stichwortBeschreibung = parameters[1];
		extractedParams.put(EAlarmDataEntries.KEYWORD_DESCRIPTION.getKey(), stichwortBeschreibung);

		// Code steht an dritter Stelle, daher [2]
		String stichwortAdditionalCode = parameters[2];
		// neuer Parameter wird den Alarm in FE2 hinzugefügt
		extractedParams.put("keyword_additional_code", stichwortAdditionalCode);
		/*
		 * Beispiel 2
		 */
		String adresse = map.get(EAlarmDataEntries.DESTINATION.getKey());
		// Komma aus Adresse entfernen
		adresse = adresse.replace(",", "");
		extractedParams.put(EAlarmDataEntries.DESTINATION.getKey(), adresse);

		// zerlegte Parameter dem Rückgabeobjekt zurückgeben
		extractorObject.setData(extractedParams);
		// Wert signalisiert, dass Zerlegung komplett ist.
		// Ist der Wert auf false oder nicht gesetzt werden die Parameter der Zerlegung NICHT übernommen.
		extractorObject.setComplete(true);

		// Diese Zeile muss ganz am Ende stehen, nachdem die eigene Zerlegung durchgefÃŒhrt wurde. Hier wird das Objekt
		// an FE2 zurückgegeben
		return extractorObject;
	}

	private boolean addID(Map<String, String> map, Map<String, String> extractedParams) {
		String externalID = map.get("externalId");

		if (StringUtils.isBlank(externalID)) {
			logger.error("Id ist leer. Alarm ist nicht komplett.");
			return false;
		}

		extractedParams.put("v2_Id", externalID);

		return true;
	}
}