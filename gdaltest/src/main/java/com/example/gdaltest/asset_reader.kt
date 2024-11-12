package com.example.gdaltest

import org.gdal.ogr.ogr
import java.nio.charset.Charset

fun readChineseTestShp1(sb: StringBuilder, path: String): Unit {
    val chineseShpDataset = ogr.Open("/vsizip/${path}/shp中文测试.shp")

    val chineseShpLayer = chineseShpDataset.GetLayer(0)
    chineseShpLayer.ResetReading()
    var chineseShpFeat = chineseShpLayer.GetNextFeature()
    while (chineseShpFeat != null) {
        sb.append("Feature: ")
        sb.append(chineseShpFeat.GetGeometryRef().ExportToWkt())
        sb.append("\n")
        sb.append("Property: ")
        sb.append(chineseShpFeat.GetFieldAsString("name"))
        sb.append("\n")
        chineseShpFeat = chineseShpLayer.GetNextFeature()
    }
    chineseShpDataset.delete()
}

fun readChineseTestShp2(sb: StringBuilder, path: String): Unit {
    val chineseShpDataset = ogr.Open(path)
    val chineseShpLayer = chineseShpDataset.GetLayer(0)
    chineseShpLayer.ResetReading()
    var chineseShpFeat = chineseShpLayer.GetNextFeature()
    while (chineseShpFeat != null) {
        sb.append("Feature: ")
        sb.append(chineseShpFeat.GetGeometryRef().ExportToWkt())
        sb.append("\n")
        sb.append("Property: ")
        sb.append(chineseShpFeat.GetFieldAsString("name"))
        sb.append("\n")
        chineseShpFeat = chineseShpLayer.GetNextFeature()
    }
    chineseShpDataset.delete()
}

fun readChineseTestShp3(sb: StringBuilder, path: String): Unit {
    val chineseShpDataset = ogr.Open(path)
    val chineseShpLayer = chineseShpDataset.GetLayer(0)
    chineseShpLayer.ResetReading()

    val nameField = chineseShpLayer.GetLayerDefn().GetFieldDefn(0)
    val nameFieldStr = nameField.GetName()
    val cnNameFieldIdx = chineseShpLayer.FindFieldIndex("中文名", 1)
    val cnNameField = chineseShpLayer.GetLayerDefn().GetFieldDefn(cnNameFieldIdx)
    val cnNameFieldStr = cnNameField.GetName()

    var chineseShpFeat = chineseShpLayer.GetNextFeature()
    while (chineseShpFeat != null) {
        sb.append("Feature: ")
        sb.append(chineseShpFeat.GetGeometryRef().ExportToWkt())
        sb.append("\n")
        sb.append("Property: ")

        // Note: Since gdal don't support gb2312 encoding by default
        val nameValue = chineseShpFeat.GetFieldAsString(0)
        val cnNameValue = chineseShpFeat.GetFieldAsString(1)
        sb.append("${nameFieldStr}=${nameValue}, ${cnNameFieldStr}=${cnNameValue}")

        sb.append("\n")
        chineseShpFeat = chineseShpLayer.GetNextFeature()
    }
    chineseShpDataset.delete()
}