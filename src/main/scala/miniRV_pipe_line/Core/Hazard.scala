package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class Hazard_IO extends Bundle {

  val isLoad_ex = Input(Bool())
  val rd_ex = Input(UInt(REG_NUMS_LOG.W))
  val rd_ls = Input(UInt(REG_NUMS_LOG.W))
  val rd_wb = Input(UInt(REG_NUMS_LOG.W))
  val wD_ex = Input(UInt(DATA_WIDTH.W))
  val wD_ls = Input(UInt(DATA_WIDTH.W))
  val wD_wb = Input(UInt(DATA_WIDTH.W))
  val isRegWrite_ex = Input(Bool())
  val isRegWrite_ls = Input(Bool())
  val isRegWrite_wb = Input(Bool())
  val rs1_id = Input(UInt(REG_NUMS_LOG.W))
  val rs2_id = Input(UInt(REG_NUMS_LOG.W))
  // branch
  val isJump_ex = Input(Bool())
  val isBranch_ex = Input(Bool())
  val resultBr_ex = Input(Bool())
  val resultC_ex = Input(UInt(ADDR_WIDTH.W))

  val stall_pc = Output(Bool())
  // val stall_if1_if2 = Output(Bool())
  val stall_if_id = Output(Bool())
  val stall_id_ex = Output(Bool())
  val stall_ex_ls = Output(Bool())
  val stall_ls_wb = Output(Bool())

  // val flush_if1_if2 = Output(Bool())
  val flush_if_id = Output(Bool())
  val flush_id_ex = Output(Bool())
  val flush_ex_ls = Output(Bool())
  val flush_ls_wb = Output(Bool())

  val pc_we = Output(Bool())
  val pc_addrTarget = Output(UInt(ADDR_WIDTH.W))
}

class Hazard extends Module {
  val io = IO(new Hazard_IO)

}
