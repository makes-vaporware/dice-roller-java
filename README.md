# dice-roller-java

Java parser for standard TTRPG dice expressions (e.g. 2d6 + 1). Runs as a command line program.

## Usage

```
                    ================================================================
                                            DICE ROLLER HELP
                    ================================================================

                    BASIC SYNTAX:
                      XdY           - Roll X dice with Y sides (e.g. 2d6, 3d20)
                      +, -, *, /    - Standard arithmetic operations
                      ( )           - Parentheses for grouping expressions

                    EXAMPLES:
                      1d20          - Roll a single 20-sided die
                      2d6 + 3       - Roll 2d6 and add 3
                      1d4 + 8d6     - Roll 1d4 and 8d6, then sum together
                      (1d8 + 2) * 2 - Roll 1d8 and add 2, then multiply sum by 2

                    ----------------------------------------------------------------

                    MODIFIERS:

                    - VALUE MODIFIERS:
                        miX         - Minimum. Set rolls below X to X. (e.g. 2d6mi3)
                        maX         - Maximum. Set rolls above X to X. (e.g. 2d6ma5)
                        eX          - Explode. Roll an additional die when you roll X (e.g. 2d6e6)

                    - KEEP MODIFIERS:
                        khX         - Keep highest X dice (e.g. 4d6kh3)
                        klX         - Keep lowest X dice (e.g. 1d20kl1)
                        k>X         - Keep all dice greater than X (e.g. 6d6k>2)
                        k<X         - Keep all dice less than X (e.g. 3d20k<15)
                        kX          - Keep all dice literally matching X (e.g. 10d6k3)

                    - DROP MODIFIERS:
                        phX         - Drop highest X dice (e.g. 2d8ph3)
                        plX         - Drop lowest X dice (e.g. 4d10pl2)
                        p>X         - Drop all dice greater than X (e.g. 4d6p>5)
                        p<X         - Drop all dice less than X (e.g. 5d10p<8)
                        pX          - Drop all dice literally matching X (e.g. 10d6p1)

                    ----------------------------------------------------------------

                    Type 'help' to bring up this guide.
                    Type 'quit' to quit the program.

                    ================================================================
```

## Examples

<img width="1390" height="510" alt="image" src="https://github.com/user-attachments/assets/0348b28f-44d4-403e-9bca-ab059d09325e" />

