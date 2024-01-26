package acal_lab04.Hw4

import chisel3._
import chisel3.util._


object ALU_funct3{
  val ADD_SUB = "b000".U
  val SLL     = 0.U
  val SLT     = 0.U
  val SLTU    = 0.U
  val XOR     = 0.U
  val SRL_SRA = 0.U
  val OR      = 0.U
  val AND     = 0.U
}

object ALU_funct7{
  val SUB_SRA = 0.U
}

import ALU_funct3._,ALU_funct7._,opcode_map._

class ALUIO extends Bundle{
  val src1    = Input(UInt(32.W))
  val src2    = Input(UInt(32.W))
  val funct3  = Input(UInt(3.W))
  val opcode  = Input(UInt(7.W))
  val out  = Output(UInt(32.W))
}

class ALU extends Module{
  val io = IO(new ALUIO)

  io.out := MuxLookup(io.funct3,0.U,Seq(
    ADD_SUB -> (io.src1+io.src2),
  ))

}