package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
class PCRegIO extends Bundle {
  val pc = Output(UInt(ADDR_WIDTH.W)) // the pc addr to output
  val pc_we = Input(Bool())
  val addrTarget = Input(UInt(ADDR_WIDTH.W)) // the addr goto
  val stall = Input(Bool())
  //debug
  // val debug_npc = Output(UInt(ADDR_WIDTH.W))
}
class PCReg extends Module {
  val io = IO(new PCRegIO())
  val regPC = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U) // pc start at START_ADDR
  val nextPc = Wire(UInt(ADDR_WIDTH.W))
  when(io.pc_we)// jump or branch
  {
    nextPc := io.addrTarget
    // io.debug_npc := io.addrTarget
  }.otherwise {
    nextPc := regPC + ADDR_BYTE_WIDTH.U // pc = pc + 4
    // io.debug_npc := regPC + ADDR_BYTE_WIDTH.U
  }
  when(!io.stall)
  {
    regPC := nextPc
  }
  io.pc := regPC
}

object myPc extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new PCReg,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
