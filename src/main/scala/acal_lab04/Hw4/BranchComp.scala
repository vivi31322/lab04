package acal_lab04.Hw4

import chisel3._
import chisel3.util._

object condition{
  val EQ = "b000".U
  val NE = "b001".U
  val LT = "b100".U
  val GE = "b101".U
  val LTU = "b110".U
  val GEU = "b111".U
}

import condition._

class BranchComp extends Module{
    val io = IO(new Bundle{
        val en = Input(Bool()) //需要 BranchComp 與否
        val funct3 = Input(UInt(3.W)) //決定比較的形式 with condition
        val src1 = Input(UInt(32.W))
        val src2 = Input(UInt(32.W))

        val brtaken = Output(Bool()) //for pc.io.brtaken
    })

    io.brtaken := io.en & MuxLookup(io.funct3,false.B,
							Seq(
								EQ -> (io.src1===io.src2), 
								NE -> (io.src1=/=io.src2),
								LT -> (io.src1.asSInt<io.src2.asSInt),//SInt小於
								GE -> (io.src1.asSInt>=io.src2.asSInt),//SInt大於等於
								LTU	-> (io.src1<io.src2),
								GEU	-> (io.src1>=io.src2)
							))

}