package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage


class PCRegIO extends Bundle {
  val pc = Output(UInt(ADDR_WIDTH.W)) // the pc addr to output
  val isJump = Input(Bool()) // if inst = jal or jalr
  val isBranch = Input(Bool()) // if inst  = branch
  val resultBr = Input(Bool()) // the branch result
  val addrTarget = Input(UInt(ADDR_WIDTH.W)) // the addr goto
  //debug
  // val debug_npc = Output(UInt(ADDR_WIDTH.W))
}
class PCReg extends Module {
  val io = IO(new PCRegIO())
  val regPC = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U) // pc start at START_ADDR
  when(io.isJump || (io.isBranch && io.resultBr)) // jump or branch
  {
    regPC := io.addrTarget
    // io.debug_npc := io.addrTarget
  }.otherwise {
    regPC := regPC + ADDR_BYTE_WIDTH.U // pc = pc + 4
    // io.debug_npc := regPC + ADDR_BYTE_WIDTH.U
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