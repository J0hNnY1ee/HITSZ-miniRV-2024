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
  val addrTarget = Input(UInt(ADDR_WIDTH.W)) // the addr goto
}
class PCReg extends Module {
  val io = IO(new PCRegIO())
  val regPC = RegInit(UInt(ADDR_WIDTH.W), START_ADDR.U) // PC从0开始
  when(io.isJump || (io.isBranch && io.resultBr)) // 跳转或者分支
  {
    regPC := io.addrTarget
  }.otherwise {
    regPC := regPC + ADDR_BYTE_WIDTH.U // 增加4
  }
  io.pc := regPC
  io.pcAdd4 := regPC + 4.U
}

object mypc extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new PCReg,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
