package com.cosminsanda

//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.functions.expr

object MyApp {

    def main(args: Array[String]): Unit = {
//        SparkSession
//          .builder()
//          .appName("Web Connector")
//          .master("local[*]")
//          .getOrCreate()
//          .read
//          .option("protocol", "http")
//          .option("host", "0.0.0.0")
//          .option("port", 8000)
//          .option("path", "/response.json")
//          .format("com.cosminsanda.spark.webservice")
//          .load()
//          .selectExpr(
//              """FROM_JSON(body, '
//                | struct<
//                |   data:array<
//                |       struct<
//                |           id:bigint
//                |       >
//                |   >
//                | >
//                |')""".stripMargin)
//          .show(truncate = false)
    }

}
