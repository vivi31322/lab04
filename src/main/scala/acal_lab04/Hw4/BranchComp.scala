package acal_lab04.Hw4

import chisel3._
import chisel3.util._

object condition{
  val EQ = 0.U
  val NE = 0.U
  val LT = 0.U
  val GE = 0.U
  val LTU = 0.U
  val GEU = 0.U
}

import condition._

class BranchComp extends Module{
    val io = IO(new Bundle{
        val en = Input(Bool())
        val funct3 = Input(UInt(3.W))
        val src1 = Input(UInt(32.W))
        val src2 = Input(UInt(32.W))

        val brtaken = Output(Bool()) //for pc.io.brtaken
    })

    //please implement your code below
    io.brtaken := false.B
}