import {
  getInterfaceInfoByIdUsingGet,
  invokeInterfaceUsingPost
} from '@/services/linksauce-backend/interfaceInfoController';
import { PageContainer } from '@ant-design/pro-components';
import { Badge, Button, Card, Descriptions, Divider, Form, Input, message } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';

const InterfaceInfo: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const [resData, setResData] = useState<any>();

  const params = useParams();

  const loadData = async () => {
    if (!params.id) {
      message.error('无数据，请重试');
    }
    setLoading(true);
    try {
      const res = await getInterfaceInfoByIdUsingGet({
        id: Number(params.id),
      });
      setData(res?.data);
      setLoading(false);
    } catch (error: any) {
      setLoading(false);
      message.error('请求失败,' + error.message);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const onFinish = async (requestData: API.InterfaceInfoInvokeRequest) => {
    if (!params.id) {
      message.error('无数据，请重试');
    }
    try {
      const res = await invokeInterfaceUsingPost({
        id: Number(params.id),
        ...requestData,
      });
      setResData(res.data);
      message.success('调用成功!');
    } catch (error: any) {
      message.error('请求失败,' + error.message);
    }
  };

  return (
    <PageContainer title={'接口详情'}>
      {data ? (
        <>
          <Card loading={loading}>
            <Descriptions title={data.name} column={2} layout="vertical" bordered={true}>
              <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
              <Descriptions.Item label="接口状态">
                {data.status === 0 ? (
                  <Badge text={'关闭'} status={'default'} />
                ) : (
                  <Badge text={'启用'} status={'processing'} />
                )}
              </Descriptions.Item>
              <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
              <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
              <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
              <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
              <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
              <Descriptions.Item label="创建时间">
                {moment(data.createTime).format('yyyy-MM-DD HH:mm:ss')}
              </Descriptions.Item>
              <Descriptions.Item label="更新时间">
                {moment(data.updateTime).format('yyyy-MM-DD HH:mm:ss')}
              </Descriptions.Item>
            </Descriptions>
          </Card>
          <Divider />
          <Card title={'在线调用'}>
            <Form name="basic" layout={'vertical'} onFinish={onFinish}>
              <Form.Item label="请求参数" name="userRequestParams">
                <Input.TextArea />
              </Form.Item>
              <Form.Item>
                <Button type="primary" htmlType="submit">
                  调用
                </Button>
              </Form.Item>
            </Form>
          </Card>
          {resData ? <Card title={'调用结果'}>{resData}</Card> : null}
        </>
      ) : (
        '接口不存在'
      )}
    </PageContainer>
  );
};

export default InterfaceInfo;
