package miniRV_single_cycle

import chisel3._
import chisel3.util._

import config.Configs._
import utils._
import _root_.circt.stage.ChiselStage
class RegFileIO extends Bundle {
  val regFileWe = Input(Bool()) // the write enable of RegFile
  val wD = Input(UInt(DATA_WIDTH.W))
  val regNum = Flipped(new BundleReg) // the number of rs1 , rs2 and rd
  val rR1 = Output(UInt(DATA_WIDTH.W))
  val rR2 = Output(UInt(DATA_WIDTH.W))
}

class RegFile extends Module {
  val io = IO(new RegFileIO())
  val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))
  when(io.regNum.rs1 === 0.U) {
    io.rR1 := 0.U
  }.otherwise{
    io.rR1 := regs(io.regNum.rs1)
  }
  when(io.regNum.rs2 === 0.U) {
    io.rR2 := 0.U
  }.otherwise
  {
    io.rR2 := regs(io.regNum.rs2)
  }

  when(io.regFileWe && io.regNum.rd =/= 0.U) {
    regs(io.regNum.rd) := io.wD
  }
}

object myrf extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new RegFile,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
