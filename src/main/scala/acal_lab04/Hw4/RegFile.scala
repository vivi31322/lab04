package acal_lab04.Hw4
import chisel3._


class RegFile(readPorts:Int) extends Module {
  val io = IO(new Bundle{
   val wen = Input(Bool())
   val waddr = Input(UInt(5.W))
   val wdata = Input(UInt(32.W))
   val raddr = Input(Vec(readPorts, UInt(5.W)))
   val rdata = Output(Vec(readPorts, UInt(32.W)))
 })

  // 1. the reset value of all regs is zero
  // val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))
  // val regs = RegInit(VecInit(Seq.fill(32)("h87654321".U(32.W))))
  
  // 2. the reset value of all regs is their index
   val regs = RegInit(VecInit(Seq.range(0,32).map{x=>x.U(32.W)}))
  //regs(1):="h87654321".U
  
  //Wiring
  (io.rdata zip io.raddr).map{case(data,addr)=>data:=regs(addr)}
  //會晚一個cycle改變值
  //regs(4):= "hffff0000".U
  when(io.wen) {regs(io.waddr) := io.wdata}
  regs(0) := 0.U
}