package miniRV_single_cycle.Core

import chisel3._
import chisel3.util._

import config.Configs._
import miniRV_single_cycle.utils.OP_TYPES._
import miniRV_single_cycle.utils.LS_TYPES._
import miniRV_single_cycle.utils._
import _root_.circt.stage.ChiselStage

class DecoderIO extends Bundle {
  val inst = Input(UInt(INST_WIDTH.W))
  val ctl = new ControlSinal()
  val imm = Output(UInt(DATA_WIDTH.W))
  val regNum = new RegNum()
}

class Decoder extends Module {
  val io = IO(new DecoderIO)

// convert regNum
  io.regNum.rs1 := io.inst(19, 15)
  io.regNum.rs2 := io.inst(24, 20)
  io.regNum.rd := io.inst(11, 7)

// convert imm
  val imm = WireDefault(0.U(32.W)) // waiting for select
  val imm_i = Cat(Fill(20, io.inst(31)), io.inst(31, 20))
  val imm_s = Cat(Fill(20, io.inst(31)), io.inst(31, 25), io.inst(11, 7))
  val imm_b = Cat(
    Fill(20, io.inst(31)),
    io.inst(7),
    io.inst(30, 25),
    io.inst(11, 8),
    0.U(1.W)
  )
  val imm_u = Cat(io.inst(31, 12), Fill(12, 0.U))
  val imm_j = Cat(
    Fill(12, io.inst(31)),
    io.inst(31),
    io.inst(19, 12),
    io.inst(20),
    io.inst(30, 21),
    Fill(1, 0.U)
  )
// imm for shift
  val imm_shift = Cat(Fill(27, 0.U), io.inst(24, 20))

// the ctrl sinals
  val isLui = WireDefault(false.B)
  val isJump = WireDefault(false.B)
  val isBranch = WireDefault(false.B)
  val isRegWrite = WireDefault(true.B)
  val isLoad = WireDefault(false.B)
  val isStore = WireDefault(false.B)
  val isSext = WireDefault(false.B)
  val isJAL = WireDefault(false.B)
  val op = WireDefault(0.U(OP_TYPES_WIDTH.W))
  val isSigned = WireDefault(true.B)
  val ctrlLSType = WireDefault(LS_W) // MemLength

  switch(io.inst(6, 2)) // last 2 digits always 0
  {
    // U: AUIPC
    is("b00101".U) {
      isSext := true.B
      isJAL := true.B // set the alua = pc
      op := OP_ADD
      imm := imm_u
    }
    // LUI
    is("b01101".U) {
      isLui := true.B
      imm := imm_u
    }
    // J: JAL
    is("b11011".U) {
      isSext := true.B
      isJump := true.B
      isJAL := true.B
      op := OP_ADD
      imm := imm_j
    }
    // I: JALR,
    // I: LB, LH, LW, LBU, LHU
    // I: ADDI, SLTI, SLTIU, XORI, ORI, ANDI, SLLI, SRLI, SRAI
    is("b11001".U, "b00000".U, "b00100".U) {
      isSext := true.B
      imm := imm_i
      // JALR
      when(io.inst(6, 2) === "b11001".U) {
        isJump := true.B
        op := OP_ADD
      }

        // LOAD
        .elsewhen(io.inst(6, 2) === "b00000".U) {
          isLoad := true.B
          op := OP_ADD
          when(io.inst(14, 12) === "b100".U | io.inst(14, 12) === "b101".U) {
            isSigned := false.B
          }
          when(io.inst(14, 12) === "b100".U | io.inst(14, 12) === "b000".U) {
            ctrlLSType := LS_B
          }
          when(io.inst(14, 12) === "b001".U | io.inst(14, 12) === "b101".U) {
            ctrlLSType := LS_H
          }
        }
        // Shift
        .elsewhen(
          io.inst(6, 2) === "b00100".U && (io.inst(14, 12) === "b001".U || io
            .inst(14, 12) === "b101".U)
        ) {
          imm := imm_shift
          switch(Cat(io.inst(30), io.inst(14, 12))) {
            // SLLI
            is("b0001".U) {
              op := OP_SLL
            }
            // SRLI
            is("b0101".U) {
              op := OP_SRL
            }
            // SRAI
            is("b1101".U) {
              op := OP_SRA
            }
          }
        }
        .otherwise {
          switch(io.inst(14, 12)) {
            // ADDI
            is("b000".U) {
              op := OP_ADD
            }
            // SLTI
            is("b010".U) {
              op := OP_LT
            }
            // SLTIU
            is("b011".U) {
              op := OP_LT
              isSigned := false.B
            }
            // XORI
            is("b100".U) {
              op := OP_XOR
            }
            // ORI
            is("b110".U) {
              op := OP_OR
            }
            // ANDI
            is("b111".U) {
              op := OP_AND
            }
          }
        }
    }
    // B: BEQ, BNE, BLT, BGE, BLTU, BGEU
    is("b11000".U) {
      isSext := false.B
      isBranch := true.B
      isRegWrite := false.B
      imm := imm_b
      switch(io.inst(14, 12)) {
        // BEQ
        is("b000".U) {
          op := OP_EQ
        }
        // BNE
        is("b001".U) {
          op := OP_NEQ
        }
        // BLT
        is("b100".U) {
          op := OP_LT
        }
        // BGE
        is("b101".U) {
          op := OP_GE
        }
        // BLTU
        is("b110".U) {
          op := OP_LT
          isSigned := false.B
        }
        // BGEU
        is("b111".U) {
          op := OP_GE
          isSigned := false.B
        }
      }
    }
    // S: SB, SH, SW
    is("b01000".U) {
      isSext := true.B
      isStore := true.B
      isRegWrite := false.B
      op := OP_ADD
      imm := imm_s
      when(io.inst(14, 12) === "b000".U) {
        ctrlLSType := LS_B
      }
      when(io.inst(14, 12) === "b001".U) {
        ctrlLSType := LS_H
      }
    }
    // R: ADD, SUB, SLL, SLT, SLTU, XOR, SRL, SRA, OR, AND
    is("b01100".U) {
      switch(io.inst(14, 12)) {
        // ADD, SUB
        is("b000".U) {
          when(io.inst(30)) {
            op := OP_SUB
          }.otherwise {
            op := OP_ADD
          }
        }
        // SLL
        is("b001".U) {
          op := OP_SLL
        }
        // SLT
        is("b010".U) {
          op := OP_LT
        }
        // SLTU
        is("b011".U) {
          op := OP_LT
          isSigned := false.B
        }
        // XOR
        is("b100".U) {
          op := OP_XOR
        }
        // SRL, SRA
        is("b101".U) {
          when(io.inst(30)) {
            op := OP_SRA
          }.otherwise {
            op := OP_SRL
          }
        }
        // OR
        is("b110".U) {
          op := OP_OR
        }
        // AND
        is("b111".U) {
          op := OP_AND
        }
      }
    }
  }
  io.ctl.isLui := isLui
  io.ctl.isSext := isSext
  io.ctl.isBranch := isBranch
  io.ctl.isJAL := isJAL
  io.ctl.isJump := isJump
  io.ctl.isLoad := isLoad
  io.ctl.op := op
  io.ctl.isRegWrite := isRegWrite
  io.ctl.isSigned := isSigned
  io.ctl.isStore := isStore
  io.ctl.ctrlLSType := ctrlLSType
  io.imm := imm
}

object myDe extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Decoder,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
