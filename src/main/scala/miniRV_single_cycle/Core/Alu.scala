package miniRV_single_cycle.Core

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import utils._
import utils.OP_TYPES._

class AluIO extends Bundle {
  val aluctl = new AluControl()
  val rR1 = Input(UInt(DATA_WIDTH.W))
  val rR2 = Input(UInt(DATA_WIDTH.W))
  val imm = Input(UInt(DATA_WIDTH.W))
  val pc = Input(UInt(ADDR_WIDTH.W))
  val resultBr = Output(Bool()) // the result of ifBranch
  val resultC = Output(UInt(DATA_WIDTH.W)) // the result of Compute
}
class Alu extends Module {
  val io = IO(new AluIO())

  val resultBr = WireDefault(false.B)
  val resultC = WireDefault(0.U(DATA_WIDTH.W))
  val A = WireDefault(0.U(DATA_WIDTH.W))
  val B = WireDefault(0.U(DATA_WIDTH.W))

  // Select data
  A := Mux(io.aluctl.isJAL, io.pc, io.rR1)
  B := Mux(io.aluctl.isSext, io.imm, io.rR2)

  switch(io.aluctl.op) {
    is(OP_NOP) { 
      resultC := 0.U
      resultBr := false.B
    }
    is(OP_ADD) { // not only add
      resultC := A +& B
    }
    is(OP_SUB) {
      resultC := A -& B
    }
    is(OP_AND) {
      resultC := A & B
    }
    is(OP_OR) {
      resultC := A | B
    }
    is(OP_XOR) {
      resultC := A ^ B
    }
    is(OP_SLL) {
      resultC := A << B(4, 0)
    }
    is(OP_SRL) {
      resultC := A >> B(4, 0)
    }
    is(OP_SRA) { 
      resultC := (A.asSInt >> B(4, 0)).asUInt
    }
    is(OP_EQ) {
      resultBr := A.asSInt === B.asSInt
      resultC := io.pc +& io.imm
    }
    is(OP_NEQ) {
      resultBr := A.asSInt =/= B.asSInt
      resultC := io.pc +& io.imm
    }
    is(OP_LT) { // 区分有符号比较和无符号比较、分支和SLT
            when(io.aluctl.isBranch) {
                when(io.aluctl.isSigned) {
                    resultBr := A.asSInt < B.asSInt
                }.otherwise {
                    resultBr := A < B
                }
                resultC := io.pc +& io.imm // the branch addr
            }.otherwise { // slt and sltu
                when(io.aluctl.isSigned) {
                    resultC := A.asSInt < B.asSInt
                }.otherwise {
                    resultC := A < B
                }
            }
        }
        is(OP_GE) { // 区分有符号比较和无符号比较
            when(io.aluctl.isSigned) {
                resultBr := A.asSInt >= B.asSInt
            }.otherwise {
                resultBr := A >= B
            }
            resultC := io.pc +& io.imm
        }
  }

  io.resultC := resultC
  io.resultBr := resultBr
}
object myAlu extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Alu,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
