package utils

import chisel3._

import config.Configs._
import utils.OP_TYPES._
import utils.LS_TYPES._

class ControlSinal extends Bundle {
  val isLui = Output(Bool())
  val isJump = Output(Bool())
  val isBranch = Output(Bool())
  val isRegWrite = Output(Bool())
  val isLoad = Output(Bool())
  val isStore = Output(Bool())
  val isSext = Output(Bool())
  val isJAL = Output(Bool())
  val op = Output(UInt(OP_TYPES_WIDTH.W))
  val isSigned = Output(Bool())
  val ctrlLSType = Output(UInt(LS_TYPE_WIDTH.W))
}

class DataMemControl extends Bundle {
  val isLoad = Input(Bool())
  val isStore = Input(Bool())
  val isSigned = Input(Bool())
  val ctrlLSType = Input(UInt(LS_TYPE_WIDTH.W))
}
class AluControl extends Bundle {
  val isJAL = Input(Bool()) // jal select pc
  val isSext = Input(Bool()) // imm and rR2
  val op = Input(UInt(OP_TYPES_WIDTH.W))
  val isSigned = Input(Bool()) // unsigned or signed
  val isBranch = Input(Bool()) // to distinguish slt and branch

}
class RfWselControl extends Bundle {
  val isJump = Input(Bool())
  val isLoad = Input(Bool())
  val isLui = Input(Bool())
}
class RegNum extends Bundle {
  val rs1 = Output(UInt(REG_NUMS_LOG.W))
  val rs2 = Output(UInt(REG_NUMS_LOG.W))
  val rd = Output(UInt(REG_NUMS_LOG.W))
}
class TraceDenug extends Bundle {
  val debug_wb_have_inst = Output(Bool())
  val debug_wb_pc = Output(UInt(ADDR_WIDTH.W))
  val debug_wb_ena = Output(Bool())
  val debug_wb_reg = Output(UInt(5.W))
  val debug_wb_value = Output(UInt(DATA_WIDTH.W))
}
