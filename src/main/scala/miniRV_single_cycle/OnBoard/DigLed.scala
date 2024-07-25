package miniRV_single_cycle.OnBoard
import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage
import config.Configs._
import utils._

class DigLedIO extends Bundle {
  val we = Input(Bool())
  val addr = Input(UInt(ADDR_WIDTH.W))
  val wD = Input(UInt(DATA_WIDTH.W))

  val dig_en = Output(UInt(DIG_LED_NUMBER.W))
  val dig_dN = new DigDN() // Segment Signals 8ps
}
class DigLed extends Module {
  val io = IO(new DigLedIO)
  val data = RegInit(0.U(DATA_WIDTH.W))
  when(io.we && io.addr === PERI_ADDR_DIG) {
    data := io.wD
  }

}

class DigDisplay extends Module {
  val io = IO(new Bundle {
    val data = Input(UInt(DATA_WIDTH.W))
    val dig_en = Input(UInt(DIG_LED_NUMBER.W))
    val dig_dN = new DigDN()
  })
  val dig_en = RegInit("b1111_1110".U(DIG_LED_NUMBER.W))
  val (cntValue, cntWrap) = Counter(1 until 200000 by 1)
  when(cntWrap) {
    dig_en := Cat(dig_en(0), dig_en(7, 1))
  } // refresh
  val hex = RegInit(0.U(4.W))
  hex := MuxLookup(dig_en, 0.U)(
    Seq(
      "b0111_1111".U -> io.data(31, 28),
      "b1011_1111".U -> io.data(27, 24),
      "b1101_1111".U -> io.data(23, 20),
      "b1110_1111".U -> io.data(19, 16),
      "b1111_0111".U -> io.data(15, 12),
      "b1111_1011".U -> io.data(11, 8),
      "b1111_1101".U -> io.data(7, 4),
      "b1111_1110".U -> io.data(3, 0),
      0.U -> 0.U
    )
  )
  val dig_a = WireDefault(0.B)
  val dig_b = WireDefault(0.B)
  val dig_c = WireDefault(0.B)
  val dig_d = WireDefault(0.B)
  val dig_e = WireDefault(0.B)
  val dig_f = WireDefault(0.B)
  val dig_g = WireDefault(0.B)
  val dig_dp = WireDefault(0.B)
  switch(hex) {
    is(0.U) {
      dig_g := 1.B
    }
    is(1.U) {
      dig_a := 1.B
      dig_d := 1.B
      dig_e := 1.B
      dig_f := 1.B
      dig_g := 1.B
    }
    is(2.U) {
      dig_c := 1.B
      dig_f := 1.B
    }
    is(3.U) {
      dig_e := 1.B
      dig_f := 1.B

    }
    is(4.U) {
      dig_a := 1.B
      dig_d := 1.B
      dig_e := 1.B

    }
    is(5.U) {
      dig_b := 1.B
      dig_e := 1.B

    }
    is(6.U) {
      dig_b := 1.B

    }
    is(7.U) {
      dig_d := 1.B
      dig_e := 1.B
      dig_f := 1.B
      dig_g := 1.B

    }
    is(9.U) {
      dig_d := 1.B
      dig_e := 1.B
    }
    is(10.U) {
      dig_d := 1.B

    }
    is(11.U) {
      dig_a := 1.B

      dig_b := 1.B

    }
    is(12.U) {
      dig_a := 1.B
      dig_b := 1.B
      dig_c := 1.B
      dig_f := 1.B

    }
    is(13.U) {
      dig_a := 1.B
      dig_f := 1.B

    }
    is(14.U) {
      dig_b := 1.B
      dig_c := 1.B

    }
    is(15.U) {
      dig_b := 1.B
      dig_c := 1.B
      dig_d := 1.B
    }
  }
  dig_dp := 1.B
  io.dig_dN.DN_A := dig_a
  io.dig_dN.DN_B := dig_b
  io.dig_dN.DN_C := dig_c
  io.dig_dN.DN_D := dig_d
  io.dig_dN.DN_E := dig_e
  io.dig_dN.DN_F := dig_f
  io.dig_dN.DN_G := dig_g
  io.dig_dN.DN_DP := dig_dp

  switch
}
