import { PageContainer } from '@ant-design/pro-components';
import { Badge, Card, Descriptions, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router';
import moment from "moment";
import {getInterfaceInfoByIdUsingGet} from "@/services/linksauce-backend/interfaceInfoController";

const InterfaceInfo: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();

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

  return (
    <PageContainer title={'接口详情'}>
      <Card loading={loading}>
        {data ? (
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
            <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{moment(data.createTime).format('yyyy-MM-DD HH:mm:ss')}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{moment(data.updateTime).format('yyyy-MM-DD HH:mm:ss')}</Descriptions.Item>
          </Descriptions>
        ) : (
          <>接口不存在</>
        )}
      </Card>
    </PageContainer>
  );
};

export default InterfaceInfo;

