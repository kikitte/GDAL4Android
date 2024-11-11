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
    val nameFieldStr = String(nameField.GetName().toByteArray(), Charset.forName("GB2312"))
    // error at Java_org_gdal_ogr_ogrJNI_FieldDefn_1GetNameRef
    //          if (result) jresult = jenv->NewStringUTF((const char *)result);
    // looks like cnNameField.GetNameRef() try to interpret a gb2312 encoding c string as utf-8 c string, which result an error.
//    val cnNameField = chineseShpLayer.GetLayerDefn().GetFieldDefn(1)
//    val cnNameFieldStr = String(cnNameField.GetNameRef().toByteArray(), Charset.forName("GB2312"))
    val cnNameFieldStr = "中文名"

    var chineseShpFeat = chineseShpLayer.GetNextFeature()
    while (chineseShpFeat != null) {
        sb.append("Feature: ")
        sb.append(chineseShpFeat.GetGeometryRef().ExportToWkt())
        sb.append("\n")
        sb.append("Property: ")

        // Note: Since gdal don't support gb2312 encoding by default
        val nameValueBytes = chineseShpFeat.GetFieldAsBinary(0)
        val nameValueStr = String(nameValueBytes, Charset.forName("GB2312"))
        val cnNameValueBytes = chineseShpFeat.GetFieldAsBinary(1)
        val cnNameValueStr = String(cnNameValueBytes, Charset.forName("GB2312"))
        sb.append("${nameFieldStr}=${nameValueStr}, ${cnNameFieldStr}=${cnNameValueStr}")

        sb.append("\n")
        chineseShpFeat = chineseShpLayer.GetNextFeature()
    }
    chineseShpDataset.delete()
}