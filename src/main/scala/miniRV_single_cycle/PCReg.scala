package miniRV_single_cycle

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
class PCRegIO extends Bundle {
  val pc = Output(UInt(ADDR_WIDTH.W)) // the pc addr to output
  val pcAdd4 = Output(UInt(ADDR_WIDTH.W))
  val isJump = Input(Bool()) // if inst = jal or jalr
  val isBranch = Input(Bool()) // if inst  = branch
  val resultBr = Input(Bool()) // the branch result
  val JumpTarget = Input(UInt(ADDR_WIDTH.W)) // the addr goto
  val ext = Input(UInt(ADDR_WIDTH.W))
}
class PCReg extends Module {
  val io = IO(new PCRegIO())
  val regPC = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U) // PC从0开始
  when(io.isJump) // 跳转或者分支
  {
    regPC := io.JumpTarget
  }.elsewhen((io.isBranch && io.resultBr)) {
    regPC := regPC + io.ext
  }.otherwise {
    regPC := regPC + ADDR_BYTE_WIDTH.U // 增加4
  }
  io.pc := regPC
  io.pcAdd4 := regPC + 4.U
}

object myPc extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new PCReg,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
