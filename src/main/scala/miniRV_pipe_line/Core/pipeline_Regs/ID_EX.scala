package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils._
import miniRV_pipe_line.utils.OP_TYPES._
import miniRV_pipe_line.utils.LS_TYPES._

class ID_EX_IO extends Bundle {
  val stall = Input(Bool())
  val flush = Input(Bool())

  val pc_id = Input(UInt(ADDR_WIDTH.W))
  val ctl_id = Flipped(new ControlSinal) // some maybe redundant
  val imm_id = Input(UInt(DATA_WIDTH.W))
  val rR1_id = Input(UInt(DATA_WIDTH.W))
  val rR2_id = Input(UInt(DATA_WIDTH.W))
  val rdNum_id = Input(UInt(REG_NUMS_LOG.W))
  val commit_id = Input(Bool())

  val pc_ex = Output(UInt(ADDR_WIDTH.W))
  val ctl_ex = new ControlSinal
  val imm_ex = Output(UInt(DATA_WIDTH.W))
  val rR1_ex = Output(UInt(DATA_WIDTH.W))
  val rR2_ex = Output(UInt(DATA_WIDTH.W))
  val rdNum_ex = Output(UInt(REG_NUMS_LOG.W))
  val commit_ex = Output(Bool())
}

class ID_EX extends Module {
  val io = IO(new ID_EX_IO())
  // initiate them use RegInit
  val rR1_ex = RegInit(0.U(DATA_WIDTH.W))
  val rR2_ex = RegInit(0.U(DATA_WIDTH.W))
  val imm_ex = RegInit(0.U(DATA_WIDTH.W))
  val pc_ex = RegInit(0.U(ADDR_WIDTH.W))
  val rdNum_ex = RegInit(0.U(REG_NUMS_LOG.W))
  val commit_ex = RegInit(false.B)
  // ctlSignal
  val isLui = RegInit(false.B)
  val isJump = RegInit(false.B)
  val isBranch = RegInit(false.B)
  val isRegWrite = RegInit(false.B)
  val isLoad = RegInit(false.B)
  val isStore = RegInit(false.B)
  val isSext = RegInit(false.B)
  val isJAL = RegInit(false.B)
  val op = RegInit(UInt(OP_TYPES_WIDTH.W), OP_NOP)
  val isSigned = RegInit(true.B)
  val ctrlLSType = RegInit(UInt(LS_TYPE_WIDTH.W), LS_W)

  when(io.flush) {
    rR1_ex := 0.U
    rR2_ex := 0.U
    imm_ex := 0.U
    pc_ex := 0.U
    rdNum_ex := 0.U
    commit_ex := 0.B
    isLui := 0.B
    isJump := 0.B
    isBranch := 0.B
    isRegWrite := 0.B
    isLoad := 0.B
    isStore := 0.B
    isSext := 0.B
    isJAL := 0.B
    isSigned := 1.B
    op := OP_NOP
    ctrlLSType := LS_W
  }.elsewhen(!io.stall) {
    rR1_ex := io.rR1_id
    rR2_ex := io.rR2_id
    imm_ex := io.imm_id
    pc_ex := io.pc_id
    rdNum_ex := io.rdNum_id
    commit_ex := io.commit_id
    isLui := io.ctl_id.isLui
    isJump := io.ctl_id.isJump
    isBranch := io.ctl_id.isBranch
    isRegWrite := io.ctl_id.isRegWrite
    isLoad := io.ctl_id.isLoad
    isStore := io.ctl_id.isStore
    isSext := io.ctl_id.isSext
    isJAL := io.ctl_id.isJAL
    isSigned := io.ctl_id.isSigned
    op := io.ctl_id.op
    ctrlLSType := io.ctl_id.ctrlLSType
  }

  io.rR1_ex := rR1_ex
  io.rR2_ex := rR2_ex
  io.imm_ex := imm_ex
  io.pc_ex := pc_ex
  io.rdNum_ex := rdNum_ex
  io.commit_ex := commit_ex
  io.ctl_ex.isLui := isLui
  io.ctl_ex.isJump := isJump
  io.ctl_ex.isBranch := isBranch
  io.ctl_ex.isRegWrite := isRegWrite
  io.ctl_ex.isLoad := isLoad
  io.ctl_ex.isStore := isStore
  io.ctl_ex.isSext := isSext
  io.ctl_ex.isJAL := isJAL
  io.ctl_ex.isSigned := isSigned
  io.ctl_ex.op := op
  io.ctl_ex.ctrlLSType := ctrlLSType
}


object myID_EX extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new ID_EX ,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}