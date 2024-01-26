package acal_lab04.Hw4

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


object wide {
  val Byte = 0.U
  val Half = 1.U
  val Word = 2.U
  val UByte = 4.U
  val UHalf = 5.U
}

import wide._

class DataMem extends Module {
  val io = IO(new Bundle {
    val funct3 = Input(UInt(32.W))
    val raddr = Input(UInt(10.W))
    val rdata = Output(SInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(10.W))
    val wdata = Input(UInt(32.W))
  })

  val memory = Mem(32, UInt(8.W))
  loadMemoryFromFile(memory, "./src/main/resource/DataMem.txt")

  io.rdata := 0.S

  val wa = WireDefault(0.U(10.W)) //address
  val wd = WireDefault(0.U(32.W)) //data

  wa := MuxLookup(io.funct3,0.U(10.W),Seq(
    Byte -> io.waddr,
    Half -> 0.U, // needs to be changed
    Word -> 0.U, // needs to be changed
  ))

  wd := MuxLookup(io.funct3,0.U,Seq(
    Byte -> 0.U, // needs to be changed
    Half -> 0.U, // needs to be changed
    Word -> io.wdata,
  ))

  when(io.wen){ //STORE
    when(io.funct3===Byte){
      memory(wa) := wd(7,0)
    }.elsewhen(io.funct3===Half){
      //Please fill in the blanks by yourself
    }.elsewhen(io.funct3===Word){
      //Please fill in the blanks by yourself
    }
  }.otherwise{ //LOAD
    io.rdata := MuxLookup(io.funct3,0.S,Seq(
      Byte -> memory(io.raddr).asSInt,
      Half -> 0.S, // needs to be changed
      Word -> 0.S, // needs to be changed
      UByte -> 0.S, // needs to be changed
      UHalf -> 0.S // needs to be changed
    ))
  }
}