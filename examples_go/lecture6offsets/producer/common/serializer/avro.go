package serializer

import (
	"fmt"
	"github.com/linkedin/goavro/v2"
)

type AvroSerializer struct {
	codec *goavro.Codec
}

func NewAvroSerializer() *AvroSerializer {
	result := &AvroSerializer{}
	var err error
	result.codec, err = goavro.NewCodec(`
        {
		 "type": "record",
		 "namespace": "io.slurm.",
		 "name": "entry",
		 "fields": [
		   { "name": "Value", "type": "string" },
		   { "name": "Country", "type": "string" }
		 ]
		 } `)
	if err != nil {
		fmt.Println(err)
	}
	return result
}

func (serializer *AvroSerializer) Serialize(object interface{}) ([]byte, error) {
	var result []byte
	return serializer.codec.BinaryFromNative(result, object)
}

func (serializer *AvroSerializer) Deserialize(data []byte, dest interface{}) error {
	var err error
	dest, _, err = serializer.codec.NativeFromBinary(data)
	return err
}
