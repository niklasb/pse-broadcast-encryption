import argparse

parser = argparse.ArgumentParser(description='Compute the Naor-Pinkas overhead')
parser.add_argument('-a', '--algorithm', help='the algorithm (default: exp)',
                    choices=('ec', 'exp'), default='exp')
parser.add_argument('-t', type=int, help='the number of revocable users')
parser.add_argument('-b', type=int, help='the bitrate')
parser.add_argument('-o', type=int, help='the overhead in percent')
parser.add_argument('-i', type=int, help='the key broadcast interval in seconds')
parser.add_argument('-q', type=int, help='parameter q of the ec or exp algorithms (default: 160 for exp, 192 for ec)')
parser.add_argument('-p', type=int, help='parameter p of the ec or exp algorithms (default: 1024 for exp, 192 for ec)')
args = parser.parse_args()

if args.algorithm == 'exp':
  q, p = args.q or 160, args.p or 1024
else: # ec
  q, p = args.q or 192, args.p or 192

print "Parameters: q=%dbits p=%dbits" % (q, p)
overhead = lambda t: t * (q + p)
overhead_rev = lambda bits: bits // (q + p)

t, b, o, i = args.t, args.b, args.o, args.i
if t != None and b != None and i != None and o is None:
  print "Overhead: %.2f%%" % (overhead(t) * 100.0 / (b * i))
elif t != None and b != None and i is None and o != None:
  print "Interval: %.2fs" % (overhead(t) * 100.0 / (b * o))
elif t != None and b is None and i != None and o != None:
  print "Bitrate: %d bits/s" % (overhead(t) * 100.0 / (i * o))
elif t is None and b != None and i != None and o != None:
  print "t = %d" % overhead_rev(float(b) * o * i / 100)
else:
  print "You need to supply exactly one less value than there are variables"
