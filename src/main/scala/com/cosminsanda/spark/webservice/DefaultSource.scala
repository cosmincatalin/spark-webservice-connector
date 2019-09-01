package com.cosminsanda.spark.webservice

import java.util

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.{DataSourceReader, InputPartition, InputPartitionReader}
import org.apache.spark.sql.sources.v2.{DataSourceOptions, DataSourceV2, ReadSupport}
import org.apache.spark.sql.types.StructType
import org.apache.spark.unsafe.types.UTF8String

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.io.{BufferedSource, Source}

class DefaultSource extends DataSourceV2 with ReadSupport {

    override def createReader(options: DataSourceOptions): DataSourceReader = {

        val protocol = options.get("protocol").orElse("https")
        val host = options.get("host").orElse("localhost")
        val port = options.getInt("port", 443)
        val path = options.get("path").orElse("")
        val method = options.get("method").orElse("GET")

        WebSourceReader(Map(
            "protocol" -> protocol,
            "host" -> host,
            "port" -> port.toString,
            "path" -> path,
            "method" -> method
        ))
    }

}


case class WebSourceReader(options: Map[String, String]) extends DataSourceReader {

    val _schema: StructType = new StructType().add("body", "string")

    override def readSchema(): StructType = _schema

    override def planInputPartitions(): util.List[InputPartition[InternalRow]] = List[InputPartition[InternalRow]](WebInputPartition(_schema, options)).asJava

}

case class WebInputPartition(requiredSchema: StructType, options: Map[String, String]) extends InputPartition[InternalRow] {

    override def createPartitionReader(): InputPartitionReader[InternalRow] = WebInputPartitionReader(requiredSchema, options)

}

case class WebInputPartitionReader(requiredSchema: StructType, options: Map[String, String]) extends InputPartitionReader[InternalRow] {

    var _called: Boolean = false
    var _result: String = ""
    val _url: String =  s"${options("protocol")}://${options("host")}:${options("port")}${options("path")}"
    val _source: BufferedSource = scala.io.Source.fromURL(_url)

    override def next(): Boolean = if (!_called) {
        _result = _source.mkString
        _called = true
        true
    } else {
        false
    }

    override def get(): InternalRow = {
        InternalRow(requiredSchema.fields.zipWithIndex.map( _ => {
            UTF8String.fromString(_result)
        }): _*)
    }

    override def close(): Unit = _source.close()
}
