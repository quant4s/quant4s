package com.bostontechnologies.quickfixs.util

import java.io.RandomAccessFile

object SequenceNumberWriter {

  def main(args: Array[String]) {
    args.toList match {
      case fileName :: senderSequenceNumber :: targetSequenceNumber :: Nil => {

        val sequenceNumberFile = new RandomAccessFile(fileName, "rw")

        sequenceNumberFile.seek(0)
        sequenceNumberFile.writeChars("" + Integer.parseInt(senderSequenceNumber) + ":" + Integer.parseInt(targetSequenceNumber))
        sequenceNumberFile.close()

        println("Sequence numbers reset")
        println(fileName + " " + senderSequenceNumber + ":" + targetSequenceNumber)
      }
      case _ => println("Usage: /sequence/number/filename senderSequenceNumber targetSequenceNumber")
    }
  }
}
