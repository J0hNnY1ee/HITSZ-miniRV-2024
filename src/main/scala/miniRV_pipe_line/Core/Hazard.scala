package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils.OP_TYPES._

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
  val isStore_id = Input(Bool())
  val isSext_id = Input(Bool())
  val isLui_id = Input(Bool())
  val isJAL_id = Input(Bool())
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
  val rs1_we = Output(Bool())
  val rs2_we = Output(Bool())
  val rs1_forward = Output(UInt(DATA_WIDTH.W))
  val rs2_forward = Output(UInt(DATA_WIDTH.W))
}

class Hazard extends Module {
  val io = IO(new Hazard_IO)
  io.flush_ls_wb := 0.B
  io.flush_ex_ls := 0.B
  io.stall_id_ex := 0.B
  io.stall_ex_ls := 0.B
  io.stall_ls_wb := 0.B
  val rD1_used = WireDefault(0.B)
  rD1_used := !(io.isLui_id && io.isJAL_id)
  val rD2_used = WireDefault(0.B)
  rD2_used := !io.isSext_id || io.isStore_id
  // RAW - A 相邻
  val RAW_A_rD1 =
    (io.rd_ex === io.rs1_id) && io.isRegWrite_ex && rD1_used && (io.rd_ex =/= 0.U);
  val RAW_A_rD2 =
    (io.rd_ex === io.rs2_id) && io.isRegWrite_ex && rD2_used && (io.rd_ex =/= 0.U);

// RAW - B 间隔一条
  val RAW_B_rD1 =
    (io.rd_ls === io.rs1_id) && io.isRegWrite_ls && rD1_used && (io.rd_ls =/= 0.U);
  val RAW_B_rD2 =
    (io.rd_ls === io.rs2_id) && io.isRegWrite_ls && rD2_used && (io.rd_ls =/= 0.U);

// RAW - C 间隔两条
  val RAW_C_rD1 =
    (io.rd_wb === io.rs1_id) && io.isRegWrite_wb && rD1_used && (io.rd_wb =/= 0.U);
  val RAW_C_rD2 =
    (io.rd_wb === io.rs2_id) && io.isRegWrite_wb && rD2_used && (io.rd_wb =/= 0.U);
  io.rs1_we := RAW_A_rD1 || RAW_B_rD1 || RAW_C_rD1
  io.rs2_we := RAW_A_rD2 || RAW_B_rD2 || RAW_C_rD2
  val rs1_forward = WireDefault(0.U(DATA_WIDTH.W))
  val rs2_forward = WireDefault(0.U(DATA_WIDTH.W))
  when(RAW_A_rD1) {
    rs1_forward := io.wD_ex
  }.elsewhen(RAW_B_rD1) {
    rs1_forward := io.wD_ls
  }.elsewhen(RAW_C_rD1) {
    rs1_forward := io.wD_wb
  }

  when(RAW_A_rD2) {
    rs2_forward := io.wD_ex
  }.elsewhen(RAW_B_rD2) {
    rs2_forward := io.wD_ls
  }.elsewhen(RAW_C_rD2) {
    rs2_forward := io.wD_wb
  }

  // load-use
  val load_use_hazard = WireDefault(0.B)
  load_use_hazard := (RAW_A_rD1 || RAW_A_rD2) && (io.isLoad_ex)

  // branch
  val control_hazard = WireDefault(0.B)
  control_hazard := io.isJump_ex || (io.isBranch_ex && io.resultBr_ex)
  io.pc_we := control_hazard
  when(load_use_hazard) {
    io.stall_pc := 1.B
    io.stall_if_id := 1.B
  }.otherwise {
    io.stall_pc :=    0.B
    io.stall_if_id := 0.B
  }

  when(control_hazard)
  {
    io.flush_if_id := 1.B
  }.otherwise
  {
    io.flush_if_id := 0.B
  }
  when(load_use_hazard || control_hazard)
  {
    io.flush_id_ex := 1.B
  }.otherwise{
    io.flush_id_ex := 0.B
  }
  io.rs1_forward := rs1_forward
  io.rs2_forward := rs2_forward
  io.pc_addrTarget := io.resultC_ex
}
object myHa extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Hazard,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
