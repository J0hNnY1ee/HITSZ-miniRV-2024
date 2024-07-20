package utils

import chisel3._

import config.Configs._
import utils.OP_TYPES._
import utils.LS_TYPES._

class ControlSinal extends Bundle {
    val isJump = Output(Bool())
    val isBranch = Output(Bool())
    val isRegWrite = Output(Bool())
    val isLoad = Output(Bool())
    val isStore = Output(Bool())
    val isSext = Output(Bool())
    val isJAL = Output(Bool())
    val OP = Output(UInt(OP_TYPES_WIDTH.W))
    val isSigned = Output(Bool())
    val ctrlLSType = Output(UInt(LS_TYPE_WIDTH.W))
}


class BundleMemDataControl extends Bundle {
    val ctrlLoad = Input(Bool())
    val ctrlStore = Input(Bool())
    val ctrlSigned = Input(Bool())
    val ctrlLSType = Input(UInt(LS_TYPE_WIDTH.W))
}
class Alu_Control extends Bundle {
    val isJAL = Input(Bool()) // jal select pc
    val isSext = Input(Bool()) // imm and rR2
    val op  = Input(UInt(OP_TYPES_WIDTH.W))
    val isSigned = Input(Bool())// unsigned or signed
    val isBranch = Input(Bool())//to distinguish slt and branch

}
class RegNum extends Bundle {
    val rs1 = Output(UInt(REG_NUMS_LOG.W))
    val rs2 = Output(UInt(REG_NUMS_LOG.W))
    val rd = Output(UInt(REG_NUMS_LOG.W))
}